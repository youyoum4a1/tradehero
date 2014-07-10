package com.tradehero.th.fragments.web;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.google.common.annotations.VisibleForTesting;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.network.NetworkConstants;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

abstract public class BaseWebViewFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_URL = BaseWebViewFragment.class.getName() + ".url";

    protected WebView webView;

    protected THIntentPassedListener parentTHIntentPassedListener;
    protected THIntentPassedListener thIntentPassedListener;
    protected THWebViewClient thWebViewClient;
    protected THWebChromeClient webChromeClient;

    public static void putUrl(@NotNull Bundle args, @NotNull String url)
    {
        args.putString(BUNDLE_KEY_URL, url);
    }

    @Nullable public static String getUrl(@Nullable Bundle args)
    {
        if (args != null)
        {
            return args.getString(BUNDLE_KEY_URL);
        }
        return null;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getLayoutResId(), container, false);
        setHasOptionsMenu(true);
        webView = (WebView) view.findViewById(R.id.webview);
        initViews(view);
        return view;
    }

    abstract protected int getLayoutResId();

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        loadUrl(getLoadingUrl());
    }

    @Nullable protected String getLoadingUrl()
    {
        return getUrl(getArguments());
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
        {
            //To fix animation on Pre Chromium WebViews such as one on ResideMenu opening animation
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        else
        {
            webView.setLayerType(View.LAYER_TYPE_NONE, null);
        }

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
                url = NetworkConstants.TRADEHERO_PROD_API_ENDPOINT + url;
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

    @VisibleForTesting
    public WebView getWebView()
    {
        return webView;
    }
}
