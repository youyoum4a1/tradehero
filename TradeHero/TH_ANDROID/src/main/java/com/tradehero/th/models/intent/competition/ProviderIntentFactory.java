package com.tradehero.th.models.intent.competition;

import android.content.Intent;
import com.tradehero.th.R;
import com.tradehero.th.models.intent.THIntentSubFactory;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by xavier on 1/20/14.
 */
public class ProviderIntentFactory extends THIntentSubFactory<ProviderIntent>
{
    public static final String TAG = ProviderIntentFactory.class.getSimpleName();

    @Inject public ProviderIntentFactory()
    {
    }

    @Override public String getHost()
    {
        return getString(R.string.intent_host_providers);
    }

    @Override public boolean isHandlableIntent(Intent intent)
    {
        return super.isHandlableIntent(intent) &&
                isHandlableHost(intent.getData().getHost());
    }

    public boolean isHandlableHost(String host)
    {
        return getHost().equals(host);
    }

    @Override public String getAction(List<String> pathSegments)
    {
        return pathSegments.get(getInteger(R.integer.intent_uri_action_provider_path_index_action));
    }

    @Override protected ProviderIntent create(Intent intent, List<String> pathSegments)
    {
        String action = getAction(pathSegments);

        ProviderIntent providerIntent = null;

        if (action.equals(getString(R.string.intent_action_provider_pages)))
        {
            providerIntent = new ProviderPageIntent(
                    ProviderPageIntent.getProviderId(pathSegments),
                    ProviderPageIntent.getForwardUriPath(pathSegments));
        }

        return providerIntent;
    }
}
