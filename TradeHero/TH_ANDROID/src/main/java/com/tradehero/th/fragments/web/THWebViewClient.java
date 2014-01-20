package com.tradehero.th.fragments.web;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentFactory;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by xavier on 1/20/14.
 */
public class THWebViewClient extends WebViewClient
{
    public static final String TAG = THWebViewClient.class.getSimpleName();

    @Inject THIntentFactory thIntentFactory;
    private final Context context;
    private THIntentPassedListener thIntentPassedListener;

    @Inject protected Lazy<ProviderListCache> providerListCache;

    public THWebViewClient(Context context)
    {
        super();
        this.context = context;
        DaggerUtils.inject(this);
    }

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        THLog.d(TAG, "shouldOverrideUrlLoading url " + url + " webView " + view);
        if (thIntentFactory.isHandlableScheme(Uri.parse(url).getScheme()))
        {
            // This is a tradehero:// scheme. Is it a ProviderPageIntent?
            THIntent thIntent = thIntentFactory.create(getPassedIntent(url));
            if (thIntent instanceof ProviderPageIntent)
            {
                url = ((ProviderPageIntent) thIntent).getCompleteForwardUriPath();
                THLog.d(TAG, "shouldOverrideUrlLoading Changed page url to " + url);
            }
            else
            {
                THLog.d(TAG, "shouldOverrideUrlLoading Notifying parent with intent");
                notifyThIntentPassed(thIntent);
                return true;
            }
        }

        THLog.d(TAG, "shouldOverrideUrlLoading Simple passing of URL");
        view.loadUrl(url);
        return false;
    }

    @Override public void onPageFinished(WebView view, String url)
    {
        super.onPageFinished(view, url);

        //view.loadUrl("javascript:window.HtmlViewer.showHTML" +
        //        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

        // TODO remove this no caching thing
        view.clearCache(true);
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
