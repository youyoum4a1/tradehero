package com.tradehero.th.fragments.web;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.network.NetworkConstants;
import java.util.Map;
import timber.log.Timber;

abstract public class BaseWebViewFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_URL = BaseWebViewFragment.class.getName() + ".url";

    protected WebView webView;
    protected ActionBar actionBar;

    protected THIntentPassedListener parentTHIntentPassedListener;
    protected THIntentPassedListener thIntentPassedListener;
    protected THWebViewClient thWebViewClient;
    protected THWebChromeClient webChromeClient;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getLayoutResId(), container, false);
        setHasOptionsMenu(true);
        webView = (WebView) view.findViewById(R.id.webview);
        initViews(view);
        return view;
    }

    abstract protected int getLayoutResId();

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        this.actionBar = getSherlockActivity().getSupportActionBar();
        this.actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        super.onCreateOptionsMenu(menu, inflater);
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

    protected void initViews(View v)
    {

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webChromeClient = new THWebChromeClient(this);
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

    protected void onProgressChanged(WebView view, int newProgress)
    {
    }

    @Override public void onDestroyView()
    {
        if (this.webChromeClient != null)
        {
            // Just to avoid calling back to destroyed fragment
            this.webChromeClient.setBaseWebViewFragment(null);
        }
        this.webChromeClient = null;
        if (this.thWebViewClient != null)
        {
            this.thWebViewClient.setThIntentPassedListener(null);
        }
        this.thWebViewClient = null;
        if (webView != null)
        {
            webView.setWebChromeClient(null);
        }
        webView = null;
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
        loadUrl(url, null);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders)
    {
        if (url != null)
        {
            if (!url.startsWith("http"))
            {
                url = NetworkConstants.TRADEHERO_PROD_ENDPOINT + url;
            }

            Timber.d("url: %s", url);
            webView.loadUrl(url, additionalHttpHeaders);
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
