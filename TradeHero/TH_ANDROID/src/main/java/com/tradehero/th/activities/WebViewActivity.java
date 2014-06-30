package com.tradehero.th.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.tradehero.common.utils.THToast;
import com.tradehero.thm.R;

/**
 * I don't know who wrote this but need to be refactor as soon as possible
 */
public class WebViewActivity extends Activity
{
    public static final String SHOW_URL = WebViewActivity.class.getName() + ".showUrl";
    public static final String HTML_DATA = WebViewActivity.class.getName() + ".htmlData";

    private ProgressBar progress;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        String url = getIntent().getStringExtra(SHOW_URL);
        String htmlData = getIntent().getStringExtra(HTML_DATA);

        WebView webView = (WebView) findViewById(R.id.webview_browser);
        progress = (ProgressBar) findViewById(android.R.id.progress);

        // Set the required Web View settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);

        webView.invokeZoomPicker();
        webView.requestFocus();
        webView.setWebViewClient(new MyWebViewClient());
        webView.setInitialScale(1);
        webView.setPadding(0, 0, 0, 0);

        if (url != null)
        {
            webView.loadUrl(url);
        }
        else if (htmlData != null)
        {
            webView.loadData(htmlData, "text/html", "");
        }
    }

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            super.onReceivedError(view, errorCode, description, failingUrl);
            THToast.show(R.string.network_error);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            progress.setVisibility(View.GONE);
        }
    }
}

