package com.tradehero.th.fragments.web;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentFactory;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SingleAttributeEvent;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class THWebViewClient extends WebViewClient
{
    @Inject THIntentFactory thIntentFactory;
    @Inject Lazy<ProviderListCacheRx> providerListCache;
    @Inject DashboardNavigator navigator;
    @Inject Analytics analytics;
    private final Context context;
    private THIntentPassedListener thIntentPassedListener;

    private boolean clearCacheAfterFinishRequest = true;

    public THWebViewClient(Context context)
    {
        super();
        this.context = context;
        HierarchyInjector.inject(context, this);
    }

    public void setClearCacheAfterFinishRequest(boolean should)
    {
        clearCacheAfterFinishRequest = should;
    }

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        Timber.d("shouldOverrideUrlLoading url %s webView %s", url, view);
        Uri uri = Uri.parse(url);
        if (thIntentFactory.isHandlableScheme(uri.getScheme()))
        {
            // This is a tradehero:// scheme. Is it a ProviderPageIntent?
            THIntent thIntent = null;
            try
            {
                thIntent = thIntentFactory.create(getPassedIntent(url));
            } catch (IndexOutOfBoundsException e)
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
                if (view.getUrl().contains(ProviderUtil.LANDING) && url.contains(ProviderUtil.RULES))
                {
                    /**
                     * HACK
                     * Assuming that the user comes from competition's landing page and ends up in the rules page.
                     * This is a good enough approximation that user had successfully joined the competition.
                     * Refer to Sai Heng.
                     */
                    analytics.fireEvent(new SingleAttributeEvent(AnalyticsConstants.CompetitionJoined, AnalyticsConstants.ProviderId,
                            String.valueOf(((ProviderPageIntent) thIntent).getProviderId().key)));
                }
            }
            else if (thIntent != null)
            {
                Timber.d("shouldOverrideUrlLoading Notifying parent with intent");
                notifyThIntentPassed(thIntent);
                return true;
            }
            else
            {
                if (uri.getHost().equalsIgnoreCase(context.getString(R.string.intent_host_home)))
                {
                    view.reload();
                    return true;
                }
                else if (uri.getHost().equalsIgnoreCase(context.getString(R.string.intent_host_web)))
                {
                    String redirectUrl = uri.getQueryParameter("url");
                    if (redirectUrl != null)
                    {
                        redirectUrl = android.net.Uri.decode(redirectUrl);
                    }
                    if (navigator != null)
                    {
                        Timber.d("Opening this page: %s", redirectUrl);
                        Bundle bundle = new Bundle();
                        WebViewFragment.putUrl(bundle, redirectUrl);
                        navigator.pushFragment(WebViewFragment.class, bundle);
                        return true;
                    }
                }
                // Need to return true, coz it will be handled by THRouter, see {@link com.tradehero.th.models.intent.THIntentFactoryImpl#create()}
                return true;
            }
        }

        if (Uri.parse(url).getScheme().equals("market"))
        {
            try
            {
                context.startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (android.content.ActivityNotFoundException anfe)
            {
                THToast.show("Unable to open url: " + url);
            }
            return true;
        }

        //Check if there's an external app to handle the protocol other than http/https.
        if(!URLUtil.isNetworkUrl(url))
        {
            Intent extIntent = new Intent(Intent.ACTION_VIEW, uri);
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> handlerActivities = packageManager.queryIntentActivities(extIntent, 0);
            if(!handlerActivities.isEmpty())
            {
                context.startActivity(extIntent);
                return true;
            }
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
