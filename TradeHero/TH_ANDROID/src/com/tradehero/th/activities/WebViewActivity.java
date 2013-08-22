package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.utills.Constants;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * I don't know who wrote this but need to be refactor as soon as possible
 */
public class WebViewActivity extends Activity
{

    private WebView mWebView;
    private ProgressBar mProgress;
    public static final String SHOW_URL = "showUrl";
    public static final String SHOW_TWITTER_URL = "TWITER_URI";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        String url = getIntent().getStringExtra(SHOW_URL);
        String url_twitter = getIntent().getStringExtra("TWITTER");
        mWebView = (WebView) findViewById(R.id.WebView_browser);

        // Set the required Web View settings
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setLoadsImagesAutomatically(true);
        mWebView.invokeZoomPicker();
        mWebView.requestFocus();
        mWebView.setWebViewClient(new MyWebViewClient(this));
        mWebView.setInitialScale(1);
        mWebView.setPadding(0, 0, 0, 0);

        if (url != null)
        {

            mWebView.loadUrl(url);
        }
        else if (url_twitter != null)
        {
            System.out.println("url_twitter===========" + url_twitter);
            mWebView.loadUrl(url_twitter);
        }
    }

    private class MyWebViewClient extends WebViewClient
    {

        private Context m_context;

        MyWebViewClient(Context context)
        {
            m_context = context;
            // pDialog = new ProgressDialog(m_context);
            mProgress = (ProgressBar) findViewById(R.id.webview_progressbar);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl)
        {
            super.onReceivedError(view, errorCode, description, failingUrl);
            THToast.show(R.string.network_error);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {

            System.out.println("should url@@@@@@@@@@@@@@@@@@@@@@@@@" + url);
            view.loadUrl(url);

            if (url.contains(Constants.TWITTER_CALLBACK_URL))
            {
                Uri uri = Uri.parse(url);
                String verifier = uri.getQueryParameter("oauth_verifier");

                finish();
            }

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);

            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            mProgress.setVisibility(View.GONE);
        }
    }
}


