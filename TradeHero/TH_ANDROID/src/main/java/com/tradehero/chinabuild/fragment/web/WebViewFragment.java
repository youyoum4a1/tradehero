package com.tradehero.chinabuild.fragment.web;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

public class WebViewFragment extends DashboardFragment
{
    public static final String BUNDLE_WEBVIEW_URL = "bundle_webview_url";
    public static final String BUNDLE_WEBVIEW_TITLE = "bundle_webview_title";

    public WebView webViewSimple;

    public String strUrl;
    public String strTitle;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        strUrl = getArguments().getString(BUNDLE_WEBVIEW_URL);
        strTitle = getArguments().getString(BUNDLE_WEBVIEW_TITLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(strTitle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.webview_simple_fragment, container, false);
        webViewSimple = (WebView)view.findViewById(R.id.webViewSimple);
        webViewSimple.setWebChromeClient(new WebChromeClient());
        webViewSimple.getSettings().setJavaScriptEnabled(true);
        webViewSimple.getSettings().setPluginState(WebSettings.PluginState.ON);
        webViewSimple.addJavascriptInterface(new CallNativeFromJS(), "CallNativeFromJS");
        loadUrl(strUrl);
        return view;
    }

    public void loadUrl(String url)
    {
        webViewSimple.loadUrl(url);
    }
}
