/**
 * WebViewFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 28, 2013
 */
package com.tradehero.th.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.api.yahoo.News;
import com.tradehero.th.fragments.base.DashboardFragment;

public class WebViewFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_URL = "url";

    private WebView webView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        setHasOptionsMenu(true);
        initViews(view);
        return view;
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.webview_menu, menu);
        getSherlockActivity().getSupportActionBar()
                .setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.webview_back:
                if (webView.canGoBack())
                {
                    webView.goBack();
                }
                break;

            case R.id.webview_forward:
                if (webView.canGoForward())
                {
                    webView.goForward();
                }
                break;

            case R.id.webview_view_in_browser:
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(webView.getUrl()));
                getActivity().startActivity(intent);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        String url = getArguments().getString(BUNDLE_KEY_URL);
        if (url != null)
        {
            webView.loadUrl(url);
        }
    }

    private void initViews(View v)
    {
        webView = (WebView) v.findViewById(R.id.webview);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(PluginState.ON);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setRenderPriority(RenderPriority.HIGH);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        WebChromeClient webChromeClient = new WebChromeClient()
        {
            @Override public void onProgressChanged(WebView view, int newProgress)
            {
                super.onProgressChanged(view, newProgress);
            }

            @Override public void onReceivedTitle(WebView view, String title)
            {
                super.onReceivedTitle(view, title);
                getSherlockActivity().getSupportActionBar().setTitle(view.getTitle());
            }
        };
        webView.setWebChromeClient(webChromeClient);

        WebViewClient webViewClient = new WebViewClient()
        {
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return false;
            }
        };
        webView.setWebViewClient(webViewClient);
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
