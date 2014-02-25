package com.tradehero.th.fragments.web;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import timber.log.Timber;

/**
 * Created by xavier on 2/25/14.
 */
abstract public class BaseWebViewFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_URL = BaseWebViewFragment.class.getName() + ".url";

    protected WebView webView;
    protected ActionBar actionBar;

    protected THIntentPassedListener parentTHIntentPassedListener;
    protected THIntentPassedListener thIntentPassedListener;
    protected THWebViewClient thWebViewClient;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getLayoutResId(), container, false);
        setHasOptionsMenu(true);
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
        webView = (WebView) v.findViewById(R.id.webview);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
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
                if (BaseWebViewFragment.this.actionBar != null && view != null) // It may be null if the fragment has already had its view destroyed
                {
                    BaseWebViewFragment.this.actionBar.setTitle(view.getTitle());
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
