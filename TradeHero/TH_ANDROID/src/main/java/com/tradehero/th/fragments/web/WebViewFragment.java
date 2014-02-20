/**
 * WebViewFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 28, 2013
 */
package com.tradehero.th.fragments.web;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import timber.log.Timber;

public class WebViewFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_URL = WebViewFragment.class.getName() + ".url";

    private WebView webView;
    private ActionBar actionBar;

    private THIntentPassedListener parentTHIntentPassedListener;
    private THIntentPassedListener thIntentPassedListener;
    private THWebViewClient thWebViewClient;

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
        this.actionBar = getSherlockActivity().getSupportActionBar();
        this.actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
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

    @Override public void onDestroyOptionsMenu()
    {
        this.actionBar = null;
        super.onDestroyOptionsMenu();
    }

    //</editor-fold>

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        loadUrl(getArguments().getString(BUNDLE_KEY_URL));
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
                if (WebViewFragment.this.actionBar != null && view != null) // It may be null if the fragment has already had its view destroyed
                {
                    WebViewFragment.this.actionBar.setTitle(view.getTitle());
                }
            }

            @Override public void onConsoleMessage(String message, int lineNumber, String sourceID)
            {
                Timber.i("%s -- From line %d of %s", message, lineNumber, sourceID);
            }

            @Override public boolean onConsoleMessage(ConsoleMessage cm)
            {
                Timber.i("%s -- From line %d of %s", cm.message(), cm.lineNumber(), cm.sourceId());
                return true;
            }
             
        };
        webView.setWebChromeClient(webChromeClient);

        this.thIntentPassedListener = new THIntentPassedListener()
        {
            @Override public void onIntentPassed(THIntent thIntent)
            {
                notifyParentIntentPassed(thIntent);
            }
        };

        this.thWebViewClient = new THWebViewClient(getActivity());
        thWebViewClient.setThIntentPassedListener(this.thIntentPassedListener);
        webView.setWebViewClient(thWebViewClient);
    }

    @Override public void onDestroyView()
    {
        if (this.thWebViewClient != null)
        {
            this.thWebViewClient.setThIntentPassedListener(null);
        }
        this.thWebViewClient = null;
        this.thIntentPassedListener = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.parentTHIntentPassedListener = null;
        super.onDestroy();
    }

    public void loadUrl(String url)
    {
        if (url != null)
        {
            Timber.d("url: %s", url);
            webView.loadUrl(url);
        }
    }

    public void setThIntentPassedListener(THIntentPassedListener thIntentPassedListener)
    {
        Timber.d("setThIntentPassedListener %s", thIntentPassedListener);
        this.parentTHIntentPassedListener = thIntentPassedListener;
    }

    private void notifyParentIntentPassed(THIntent thIntent)
    {
        THIntentPassedListener parentListenerCopy = this.parentTHIntentPassedListener;
        if (parentListenerCopy != null)
        {
            parentListenerCopy.onIntentPassed(thIntent);
        }
        else
        {
            Timber.d("notifyParentIntentPassed listener is null");
        }
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
