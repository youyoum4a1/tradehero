package com.tradehero.th.fragments.web;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tradehero.common.utils.THToast;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import timber.log.Timber;

public class THWebViewClient extends WebViewClient
{
    @Inject protected Lazy<ProviderListCacheRx> providerListCache;
    @Inject protected DashboardNavigator navigator;
    //TODO Change Analytics
    //@Inject protected Analytics analytics;
    protected final Context context;

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
}
