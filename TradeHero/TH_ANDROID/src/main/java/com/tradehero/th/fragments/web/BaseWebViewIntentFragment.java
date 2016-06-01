package com.ayondo.academy.fragments.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.ayondo.academy.models.intent.THIntent;
import com.ayondo.academy.models.intent.THIntentPassedListener;
import com.ayondo.academy.network.NetworkConstants;
import java.util.Map;
import timber.log.Timber;

public class BaseWebViewIntentFragment extends BaseWebViewFragment
{
    protected THIntentPassedListener parentTHIntentPassedListener;
    protected THIntentPassedListener thIntentPassedListener;

    @SuppressLint("NewApi")
    @Override protected void initViews(View v)
    {
        super.initViews(v);
        this.thIntentPassedListener = new THIntentPassedListener()
        {
            @Override public void onIntentPassed(THIntent thIntent)
            {
                BaseWebViewIntentFragment.this.notifyParentIntentPassed(thIntent);
            }
        };
        ((THWebViewIntentClient) webViewClient).setThIntentPassedListener(this.thIntentPassedListener);
    }

    @NonNull protected WebViewClient createWebViewClient()
    {
        return new THWebViewIntentClient(getActivity());
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
        if (this.webViewClient instanceof THWebViewIntentClient)
        {
            ((THWebViewIntentClient) this.webViewClient).setThIntentPassedListener(null);
        }
        this.thIntentPassedListener = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.parentTHIntentPassedListener = null;
        super.onDestroy();
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
}
