package com.androidth.general.fragments.web;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.androidth.general.fragments.competition.CompetitionWebViewFragment;

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
        if (!(fragmentCopy instanceof CompetitionWebViewFragment) && fragmentCopy != null && fragmentCopy.shouldDisplayTitleInActionBar())
        {
            fragmentCopy.setActionBarTitle(view.getTitle());
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
