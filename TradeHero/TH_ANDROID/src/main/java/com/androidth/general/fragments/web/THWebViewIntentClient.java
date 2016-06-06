package com.androidth.general.fragments.web;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.models.intent.THIntent;
import com.androidth.general.models.intent.THIntentFactory;
import com.androidth.general.models.intent.THIntentPassedListener;
import com.androidth.general.models.intent.competition.ProviderPageIntent;

import javax.inject.Inject;

import timber.log.Timber;

public class THWebViewIntentClient extends THWebViewClient
{
    @Inject THIntentFactory thIntentFactory;
    private THIntentPassedListener thIntentPassedListener;

    public THWebViewIntentClient(Context context)
    {
        super(context);
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
                    //TODO Analytics change
                    //analytics.fireEvent(new SingleAttributeEvent(AnalyticsConstants.CompetitionJoined, AnalyticsConstants.ProviderId, String.valueOf(((ProviderPageIntent) thIntent).getProviderId().key)));
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
                        redirectUrl = Uri.decode(redirectUrl);
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
                // Need to return true, coz it will be handled by THRouter, see {@link com.androidth.general.models.intent.THIntentFactoryImpl#create()}
                return true;
            }
        }


        return super.shouldOverrideUrlLoading(view, url);
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
