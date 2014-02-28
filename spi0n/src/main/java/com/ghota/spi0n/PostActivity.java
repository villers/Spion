package com.ghota.spi0n;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ghota.spi0n.model.PostData;

public class PostActivity extends ActionBarActivity {

    protected PostData post;
    protected FrameLayout webViewPlaceholder;
    protected WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // recupere le Bundle
        post = getIntent().getExtras().getParcelable("post");

        setTitle(post.postTitle);


        // Initialize l'UI
        initUI();
    }

    private void initUI() {
        webViewPlaceholder = ((FrameLayout)findViewById(R.id.webViewPlaceholder));

        // Initialize the WebView if necessary
        if (webView == null)
        {
            webView = new WebView(this);
            webView.setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            webView.setScrollbarFadingEnabled(true);

            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });

            webView.setWebChromeClient(new WebChromeClient() {

                FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                public View mCustomView;
                public LinearLayout mContentView;
                public FrameLayout mCustomViewContainer;
                public CustomViewCallback mCustomViewCallback;

                @Override
                public void onShowCustomView(View view, CustomViewCallback callback) {

                    if (mCustomView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }
                    mContentView = (LinearLayout) findViewById(R.id.LinearLayoutPost);
                    mContentView.setVisibility(View.GONE);
                    mCustomViewContainer = new FrameLayout(PostActivity.this);
                    mCustomViewContainer.setLayoutParams(LayoutParameters);
                    mCustomViewContainer.setBackgroundResource(android.R.color.black);
                    view.setLayoutParams(LayoutParameters);
                    mCustomViewContainer.addView(view);
                    mCustomView = view;
                    mCustomViewCallback = callback;
                    mCustomViewContainer.setVisibility(View.VISIBLE);
                    setContentView(mCustomViewContainer);
                }

                @Override
                public void onHideCustomView() {
                    super.onHideCustomView();
                    if (mCustomView == null) {
                        return;
                    } else {
                        // Hide the custom view.
                        mCustomView.setVisibility(View.GONE);
                        // Remove the custom view from its container.
                        mCustomViewContainer.removeView(mCustomView);
                        mCustomView = null;
                        mCustomViewContainer.setVisibility(View.GONE);
                        mCustomViewCallback.onCustomViewHidden();
                        // Show the content view.
                        mContentView.setVisibility(View.VISIBLE);

                        setContentView(mContentView);
                    }
                }
            });

            webView.loadUrl(AppConstants.URL_ARTICLE_HTML+post.postGuid);
        }

        webViewPlaceholder.addView(webView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        if (webView != null)
        {
            // Remove the WebView from the old placeholder
            webViewPlaceholder.removeView(webView);
        }

        super.onConfigurationChanged(newConfig);

        // Load the layout resource for the new configuration
        setContentView(R.layout.activity_post);

        // Reinitialize the UI
        initUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of the WebView
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the state of the WebView
        webView.restoreState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        toggleWebViewState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleWebViewState(false);
    }

    private void toggleWebViewState(boolean pause){
        try
        {
            Class.forName("android.webkit.WebView")
                    .getMethod(pause
                            ? "onPause"
                            : "onResume", (Class[]) null)
                    .invoke(webView, (Object[]) null);
        }
        catch (Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
