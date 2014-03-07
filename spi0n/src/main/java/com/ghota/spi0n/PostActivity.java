package com.ghota.spi0n;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.ghota.myhtml5webview.HTML5WebView;
import com.ghota.spi0n.database.PostsBDD;
import com.ghota.spi0n.model.PostData;


public class PostActivity extends ActionBarActivity {
    HTML5WebView mWebView;
    PostData post;
    PostsBDD postsBDD;
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postsBDD = new PostsBDD(this);
        post = getIntent().getExtras().getParcelable("post");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(post.postTitle);

        mWebView = new HTML5WebView(this);

        if (savedInstanceState != null)
            mWebView.restoreState(savedInstanceState);
        else
            mWebView.loadUrl(AppConstants.URL_ARTICLE_HTML+post.postGuid);

        setContentView(mWebView.getLayout());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mWebView.stopLoading();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

        @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.inCustomView()) {
                mWebView.hideCustomView();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(shareItem);
        doShare(new Intent(Intent.ACTION_SEND));

        return super.onCreateOptionsMenu(menu);
    }

    public void doShare(Intent shareIntent) {

        StringBuilder message = new StringBuilder();
        message.append(post.postTitle).append("\r\n\r\n");
        message.append(Html.fromHtml(post.postExcerpt)).append("\r\n");
        message.append(post.postUrl).append("\r\n");

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message.toString());
        mShareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mWebView.inCustomView()) {
                    mWebView.hideCustomView();
                    break;
                }
                finish();
                break;

            case R.id.action_refresh:
                mWebView.reload();
                return true;

            case R.id.action_add_favoris :
                postsBDD.open();

                if(postsBDD.getPostWithID(Integer.valueOf(post.postGuid)) != null){
                    postsBDD.removePostWithID(Integer.valueOf(post.postGuid));
                    Toast.makeText(PostActivity.this, "Suppresion du favoris", Toast.LENGTH_SHORT).show();
                }
                else{
                    postsBDD.insertPost(post);
                    Toast.makeText(PostActivity.this, "Ajout du favoris", Toast.LENGTH_SHORT).show();
                }
                invalidateOptionsMenu();
                postsBDD.close();
                
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        postsBDD.open();

        MenuItem favoris = menu.findItem(R.id.action_add_favoris);
        if(postsBDD.getPostWithID(Integer.valueOf(post.postGuid)) != null)
            favoris.setIcon(android.R.drawable.btn_star_big_on);
        else
            favoris.setIcon(android.R.drawable.btn_star_big_off);

        postsBDD.close();
        return super.onPrepareOptionsMenu(menu);
    }
}