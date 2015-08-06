package com.tradehero.chinabuild.fragment.web;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

public class WebViewFragment extends DashboardFragment {
    public static final String BUNDLE_WEBVIEW_URL = "bundle_webview_url";
    public static final String BUNDLE_WEBVIEW_TITLE = "bundle_webview_title";

    public WebView webViewSimple;

    public String strUrl;
    public String strTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strUrl = getArguments().getString(BUNDLE_WEBVIEW_URL);
        strTitle = getArguments().getString(BUNDLE_WEBVIEW_TITLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(strTitle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_layout, container, false);
        webViewSimple = (WebView) view.findViewById(R.id.webViewSimple);
        webViewSimple.setWebChromeClient(new WebChromeClient());
        webViewSimple.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewSimple.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        webViewSimple.getSettings().setJavaScriptEnabled(true);
        webViewSimple.getSettings().setPluginState(WebSettings.PluginState.ON);
        webViewSimple.addJavascriptInterface(new CallNativeFromJS(), "CallNativeFromJS");
        webViewSimple.loadUrl(strUrl);
        return view;
    }

}
