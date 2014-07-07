package com.tradehero.th.fragments.web;

import android.app.ActionBar;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import timber.log.Timber;

public class THWebChromeClient extends WebChromeClient
{
    private BaseWebViewFragment baseWebViewFragment;

    public THWebChromeClient(BaseWebViewFragment baseWebViewFragment)
    {
        super();
        this.baseWebViewFragment = baseWebViewFragment;
    }

    public void setBaseWebViewFragment(BaseWebViewFragment baseWebViewFragment)
    {
        this.baseWebViewFragment = baseWebViewFragment;
    }

    @Override public void onProgressChanged(WebView view, int newProgress)
    {
        super.onProgressChanged(view, newProgress);
        BaseWebViewFragment fragmentCopy = baseWebViewFragment;
        if (fragmentCopy != null)
        {
            fragmentCopy.onProgressChanged(view, newProgress);
        }
    }

    @Override public void onReceivedTitle(WebView view, String title)
    {
        super.onReceivedTitle(view, title);
        BaseWebViewFragment fragmentCopy = baseWebViewFragment;
        if (fragmentCopy != null && fragmentCopy.getSherlockActivity().getActionBar() != null)
        {
            ActionBar actionBar = fragmentCopy.getSherlockActivity().getActionBar();
            actionBar.setTitle(view.getTitle());
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
}
