package com.ghota.spi0n;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ghota.spi0n.Adapter.PostItemAdapter;
import com.ghota.spi0n.database.PostsBDD;
import com.ghota.spi0n.model.PostData;

import java.util.ArrayList;

public class ShowFavorisActivity extends ActionBarActivity {

    private ListView favListView;
    private ArrayList<PostData> listData;
    private PostsBDD postsBDD;
    private PostItemAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_favoris);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listData = new ArrayList<PostData>();
        postsBDD = new PostsBDD(this);
        postsBDD.open();
        listData.addAll(postsBDD.getAllPosts());
        postsBDD.close();

        favListView = (ListView) findViewById(R.id.favListView);
        postAdapter = new PostItemAdapter(ShowFavorisActivity.this, R.layout.postitem, listData);
        favListView.setAdapter(postAdapter);

        favListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                PostData data = listData.get(arg2);

                if(MainActivity.mPreference.getString("open_post_type", "1").equals("1")){
                    Intent intent = new Intent(ShowFavorisActivity.this, PostActivity.class);
                    intent.putExtra("post", data);
                    startActivity(intent);
                }
                else if (MainActivity.mPreference.getString("open_post_type", "1").equals("0")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(AppConstants.URL_ARTICLE_HTML + data.postGuid));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.show_favoris, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;

            case R.id.remove_all:
                postsBDD.open();
                postsBDD.removeAllPosts();
                postsBDD.close();

                postAdapter.clear();
                postAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        postsBDD.open();
        if(postAdapter.getCount() != postsBDD.countPost()){
            postAdapter.clear();
            listData.clear();
            listData.addAll(postsBDD.getAllPosts());
            postAdapter.notifyDataSetChanged();
        }
        postsBDD.close();
    }
}
