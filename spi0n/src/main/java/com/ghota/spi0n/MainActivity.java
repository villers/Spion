package com.ghota.spi0n;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ghota.dragtorefresh.RefreshableInterface;
import com.ghota.dragtorefresh.RefreshableListView;
import com.ghota.spi0n.Adapter.PostItemAdapter;
import com.ghota.spi0n.Utils.ServiceHandler;
import com.ghota.spi0n.model.PostData;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    public static SharedPreferences mPreference;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreference = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize ImageLoader avec la configuration.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch(number){
            case 0:
            case 1:
                mTitle = getResources().getStringArray(R.array.arr_left_nav_list)[0];
                break;
            default:
                mTitle = getResources().getStringArray(R.array.arr_left_nav_list)[--number];
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements RefreshableInterface {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private ArrayList<String> guidList;
        private ArrayList<PostData> listData;
        private RefreshableListView postListView;
        private PostItemAdapter postAdapter;
        private int pagnation = 1; // start from 1
        private boolean isRefreshLoading = true;
        private boolean isLoading = false;
        private StringBuilder categorie_url;

        // contacts JSONArray
        JSONArray posts = null;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber-1);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            RefreshableListView listView = (RefreshableListView) inflater.inflate(R.layout.fragment_main, container, false);

            categorie_url = new StringBuilder();
            categorie_url.append(AppConstants.RSS_SERVER_URL);
            categorie_url.append(getResources().getStringArray(R.array.tag_categorie)[getArguments().getInt(ARG_SECTION_NUMBER)]);

            guidList = new ArrayList<String>();
            listData = new ArrayList<PostData>();
            postListView = (RefreshableListView) listView.findViewById(R.id.postListView);
            postAdapter = new PostItemAdapter(getActivity(), R.layout.postitem, listData);
            postListView.setAdapter(postAdapter);
            postListView.setOnRefresh(this);
            postListView.onRefreshStart();
            postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    PostData data = listData.get(arg2 - 1);

                    if(mPreference.getString("open_post_type", "1").equals("1"))
                    {
                        Intent intent = new Intent(getActivity(), PostActivity.class);
                        /*intent.putExtra("postGuid", data.postGuid);
                        intent.putExtra("postSlug", data.postSlug);
                        intent.putExtra("postUrl", data.postUrl);
                        intent.putExtra("postTitle", data.postTitle);
                        intent.putExtra("postContent", data.postContent);
                        intent.putExtra("postExcerpt", data.postExcerpt);
                        intent.putExtra("postDate", data.postDate);
                        intent.putExtra("postCategory", data.postCategory);
                        intent.putExtra("postComment", data.postComment);
                        intent.putExtra("postThumbUrl", data.postThumbUrl);*/
                        intent.putExtra("post", data);
                        startActivity(intent);
                    }
                    else if (mPreference.getString("open_post_type", "1").equals("0"))
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(AppConstants.URL_ARTICLE_HTML+data.postGuid));
                        startActivity(intent);
                    }

                }
            });

            return listView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void startFresh() {
            // TODO Auto-generated method stub
            if (!isLoading) {
                isRefreshLoading = true;
                isLoading = true;
                // Toast.makeText(getActivity(), categorie_url.toString() + AppConstants.RSS_SERVER_TO_JSON, Toast.LENGTH_SHORT).show();
                new GetPostJson().execute(categorie_url.toString() + AppConstants.RSS_SERVER_TO_JSON);
            } else {
                postListView.onRefreshComplete();
            }
        }

        @Override
        public void startLoadMore() {
            // TODO Auto-generated method stub
            if (!isLoading) {
                isRefreshLoading = false;
                isLoading = true;
                // Toast.makeText(getActivity(), categorie_url.toString() + "page/" + (pagnation +1) + "/" + AppConstants.RSS_SERVER_TO_JSON, Toast.LENGTH_SHORT).show();
                new GetPostJson().execute(categorie_url.toString() + "page/" + (++pagnation) + "/" + AppConstants.RSS_SERVER_TO_JSON);
            } else {
                postListView.onLoadingMoreComplete();
            }
        }


        /**
         * Async task class to get json by making HTTP call
         * */
        private class GetPostJson extends AsyncTask<String, Integer, ArrayList<PostData>> {

            private ArrayList<PostData> listArray;

            @Override
            protected ArrayList<PostData> doInBackground(String... params) {
                String jsonUrl = params[0];

                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(jsonUrl, ServiceHandler.GET);

                Log.d("Response: ", "> " + jsonStr);

                if (jsonStr != null) {
                    try {
                        listArray = new ArrayList<PostData>();
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        posts = jsonObj.getJSONArray("posts");

                        // looping through All Contacts
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject c = posts.getJSONObject(i);

                            PostData objItem = new PostData();
                            objItem.postGuid = String.valueOf(c.getInt("id"));
                            objItem.postSlug = c.getString("slug");
                            objItem.postUrl = c.getString("url");
                            objItem.postTitle = Html.fromHtml(c.getString("title")).toString();
                            objItem.postContent = c.getString("content");
                            objItem.postExcerpt = c.getString("excerpt");
                            objItem.postDate = c.getString("date");
                            objItem.postCategory = c.getJSONArray("categories").getJSONObject(0).getString("title");
                            objItem.postComment = c.getString("comment_count");
                            objItem.postThumbUrl = c.getString("thumbnail");

                            listArray.add(objItem);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    Toast.makeText(getActivity(), "Impossible d'obtenir des données à partir du site.", Toast.LENGTH_SHORT).show();
                }

                return listArray;
            }

            @Override
            protected void onPostExecute(ArrayList<PostData> result) {

                boolean isupdated = false;
                for (int i = 0; i < result.size(); i++) {
                    // check if the post is already in the list
                    if (guidList.contains(result.get(i).postGuid)) {
                        continue;
                    } else {
                        isupdated = true;
                        guidList.add(result.get(i).postGuid);
                    }

                    if (isRefreshLoading) {
                        listData.add(i, result.get(i));
                    } else {
                        listData.add(result.get(i));
                    }
                }

                if (isupdated) {
                    postAdapter.notifyDataSetChanged();
                }

                isLoading = false;

                if (isRefreshLoading) {
                    postListView.onRefreshComplete();
                } else {
                    postListView.onLoadingMoreComplete();
                }

                super.onPostExecute(result);
            }

        }

    }
}
