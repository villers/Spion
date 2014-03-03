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
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ghota.mydragtorefresh.RefreshableInterface;
import com.ghota.mydragtorefresh.RefreshableListView;
import com.ghota.spi0n.Adapter.PostItemAdapter;
import com.ghota.spi0n.Utils.Network;
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

    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static SharedPreferences mPreference;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreference = PreferenceManager.getDefaultSharedPreferences(this);

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
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
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

        getMenuInflater().inflate(R.menu.main, menu);
        restoreActionBar();

        MenuItem searchItem = menu.findItem(R.id.itemSearch);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(s)).commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements RefreshableInterface {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_SEARCH = "section_seach";
        private ArrayList<String> guidList;
        private ArrayList<PostData> listData;
        private RefreshableListView postListView;
        private PostItemAdapter postAdapter;
        private int pagnation = 1;
        private boolean isRefreshLoading = true;
        private boolean isLoading = false;
        private StringBuilder categorie_url;

        JSONArray posts = null;

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber-1);
            args.putString(ARG_SECTION_SEARCH, "");
            fragment.setArguments(args);
            return fragment;
        }

        public static PlaceholderFragment newInstance(String search) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, 0);
            args.putString(ARG_SECTION_SEARCH, search);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {}

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

                    if(mPreference.getString("open_post_type", "1").equals("1")){
                        Intent intent = new Intent(getActivity(), PostActivity.class);
                        intent.putExtra("post", data);
                        startActivity(intent);
                    }
                    else if (mPreference.getString("open_post_type", "1").equals("0")){
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
            if (!isLoading) {
                isRefreshLoading = true;
                isLoading = true;
                if (Network.isNetworkAvailable(getActivity()))
                    new GetPostJson().execute(categorie_url.toString() + AppConstants.RSS_SERVER_TO_JSON + "&s="+ getArguments().getString(ARG_SECTION_SEARCH) );
                else{
                    Toast.makeText(getActivity(), getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    postListView.onRefreshComplete();
                }
            } else
                postListView.onRefreshComplete();
        }

        @Override
        public void startLoadMore() {
            if (!isLoading) {
                isRefreshLoading = false;
                isLoading = true;
                if (Network.isNetworkAvailable(getActivity()))
                    new GetPostJson().execute(categorie_url.toString() + "page/" + (++pagnation) + "/" + AppConstants.RSS_SERVER_TO_JSON + "&s="+ getArguments().getString(ARG_SECTION_SEARCH));
                else{
                    Toast.makeText(getActivity(), getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    postListView.onLoadingMoreComplete();
                }
            } else
                postListView.onLoadingMoreComplete();
        }

        private class GetPostJson extends AsyncTask<String, Integer, ArrayList<PostData>> {

            private ArrayList<PostData> listArray;

            @Override
            protected ArrayList<PostData> doInBackground(String... params) {
                String jsonUrl = params[0];

                ServiceHandler sh = new ServiceHandler();
                String jsonStr = sh.makeServiceCall(jsonUrl, ServiceHandler.GET);

                Log.d("Response: ", "> " + jsonStr);

                if (jsonStr != null) {
                    try {
                        listArray = new ArrayList<PostData>();

                        JSONObject jsonObj = new JSONObject(jsonStr);
                        posts = jsonObj.getJSONArray("posts");

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
                            objItem.postCategory = Html.fromHtml(c.getJSONArray("categories").getJSONObject(0).getString("title")).toString();
                            objItem.postComment = c.getString("comment_count");
                            objItem.postThumbUrl = c.getString("thumbnail");

                            listArray.add(objItem);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }

                return listArray;
            }

            @Override
            protected void onPostExecute(ArrayList<PostData> result) {

                boolean isupdated = false;
                for (int i = 0; i < result.size(); i++) {
                    // check if the post is already in the list
                    if (guidList.contains(result.get(i).postGuid))
                        continue;
                    else {
                        isupdated = true;
                        guidList.add(result.get(i).postGuid);
                    }

                    if (isRefreshLoading)
                        listData.add(i, result.get(i));
                    else
                        listData.add(result.get(i));
                }

                if (isupdated)
                    postAdapter.notifyDataSetChanged();

                isLoading = false;

                if (isRefreshLoading)
                    postListView.onRefreshComplete();
                else
                    postListView.onLoadingMoreComplete();

                super.onPostExecute(result);
            }
        }
    }
}
