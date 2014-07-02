package com.tradehero.th.fragments.web;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentFactory;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

public class THWebViewClient extends WebViewClient
{
    @Inject THIntentFactory thIntentFactory;
    private final Context context;
    private THIntentPassedListener thIntentPassedListener;

    @Inject Lazy<ProviderListCache> providerListCache;
    private boolean clearCacheAfterFinishRequest = true;

    public THWebViewClient(Context context)
    {
        super();
        this.context = context;
        DaggerUtils.inject(this);
    }

    public void setClearCacheAfterFinishRequest(boolean should)
    {
        clearCacheAfterFinishRequest = should;
    }

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        Timber.d("shouldOverrideUrlLoading url %s webView %s", url, view);
        if (thIntentFactory.isHandlableScheme(Uri.parse(url).getScheme()))
        {
            // This is a tradehero:// scheme. Is it a ProviderPageIntent?
            THIntent thIntent = null;
            try
            {
                thIntent = thIntentFactory.create(getPassedIntent(url));
            }
            catch (IndexOutOfBoundsException e)
            {
                Timber.e(e, "Failed to create intent with string %s", url);
            }
            if (thIntent instanceof ProviderPageIntent)
            {
                // Somewhat of a HACK to make sure we reload the competition
                // providers after a successful enrollment
                providerListCache.get().invalidateAll();
                url = ((ProviderPageIntent) thIntent).getCompleteForwardUriPath();
                Timber.d("shouldOverrideUrlLoading Changed page url to %s", url);
            }
            else
            {
                Timber.d("shouldOverrideUrlLoading Notifying parent with intent");
                notifyThIntentPassed(thIntent);
                return true;
            }
        }

        if (Uri.parse(url).getScheme().equals("market"))
        {
            try
            {
                context.startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
            catch (android.content.ActivityNotFoundException anfe)
            {
                THToast.show("Unable to open url: " + url);
            }
            return true;
        }

        view.loadUrl(url);
        Timber.d("shouldOverrideUrlLoading Simple passing of URL");
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override public void onPageFinished(WebView view, String url)
    {
        super.onPageFinished(view, url);

        //view.loadUrl("javascript:window.HtmlViewer.showHTML" +
        //        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

        // TODO remove this no caching thing
        if (clearCacheAfterFinishRequest)
        {
            view.clearCache(true);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
    {
        // TODO, maybe we should not ignore ssl error if we are in production mode, to protect our user
        handler.proceed();
    }

    public Intent getPassedIntent(String url)
    {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    public void setThIntentPassedListener(THIntentPassedListener thIntentPassedListener)
    {
        this.thIntentPassedListener = thIntentPassedListener;
    }

    private void notifyThIntentPassed(THIntent thIntent)
    {
        THIntentPassedListener listenerCopy = this.thIntentPassedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onIntentPassed(thIntent);
        }
    }
}
