package com.ayondo.academy.fragments.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.common.annotations.VisibleForTesting;
import com.tradehero.common.utils.SDKUtils;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.base.BaseFragment;
import com.ayondo.academy.network.NetworkConstants;
import com.ayondo.academy.utils.Constants;
import java.util.Map;
import timber.log.Timber;

public class BaseWebViewFragment extends BaseFragment
{
    private static final String BUNDLE_KEY_URL = BaseWebViewFragment.class.getName() + ".url";

    protected WebView webView;
    protected WebViewClient webViewClient;
    protected THWebChromeClient webChromeClient;

    public static void putUrl(@NonNull Bundle args, @NonNull String url)
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
        return inflater.inflate(getLayoutResId(), container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        webView = (WebView) view.findViewById(R.id.webview);
        initViews(view);
    }

    @LayoutRes protected int getLayoutResId()
    {
        return R.layout.fragment_webview;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        loadUrl(getLoadingUrl());
    }

    @Nullable protected String getLoadingUrl()
    {
        return getUrl(getArguments());
    }

    @SuppressLint("NewApi") @CallSuper
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

        if (SDKUtils.isKitKatOrHigher() && !Constants.RELEASE)
        {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        if (SDKUtils.isKitKatOrHigher())
        {
            webView.setLayerType(View.LAYER_TYPE_NONE, null);
        }
        else
        {
            //To fix animation on Pre Chromium WebViews.
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack())
                {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
        webChromeClient = new THWebChromeClient(this);
        webView.setWebChromeClient(webChromeClient);

        this.webViewClient = createWebViewClient();
        webView.setWebViewClient(webViewClient);
    }

    @NonNull protected WebViewClient createWebViewClient()
    {
        return new THWebViewClient(getActivity());
    }

    protected void onProgressChanged(WebView view, int newProgress)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            activity.setProgress(newProgress * 100);
        }
    }

    @Override public void onDestroyView()
    {
        if (this.webChromeClient != null)
        {
            // Just to avoid calling back to destroyed fragment
            this.webChromeClient.setBaseWebViewFragment(null);
        }
        this.webChromeClient = null;
        this.webViewClient = null;
        if (webView != null)
        {
            webView.setWebChromeClient(null);
        }
        webView = null;
        super.onDestroyView();
    }

    public void loadUrl(@Nullable String url)
    {
        loadUrl(url, null);
    }

    public void loadUrl(@Nullable String url, @Nullable Map<String, String> additionalHttpHeaders)
    {
        if (url != null)
        {
            if (!url.startsWith("http"))
            {
                url = NetworkConstants.getApiEndPointInUse() + url;
            }

            Timber.d("url: %s", url);
            webView.loadUrl(url, additionalHttpHeaders);
        }
    }

    @VisibleForTesting
    public WebView getWebView()
    {
        return webView;
    }

    public boolean shouldDisplayTitleInActionBar()
    {
        return true;
    }
}
