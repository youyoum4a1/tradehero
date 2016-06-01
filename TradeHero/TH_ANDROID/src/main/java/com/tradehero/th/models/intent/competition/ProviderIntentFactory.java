package com.ayondo.academy.models.intent.competition;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.models.intent.THIntentSubFactory;
import java.util.List;
import javax.inject.Inject;

public class ProviderIntentFactory extends THIntentSubFactory<ProviderIntent>
{
    @Inject public ProviderIntentFactory(@NonNull Context context)
    {
        super(context.getResources());
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
                    resources,
                    ProviderPageIntent.getProviderId(resources, pathSegments),
                    ProviderPageIntent.getForwardUriPath(resources, pathSegments));
        }

        return providerIntent;
    }
}
