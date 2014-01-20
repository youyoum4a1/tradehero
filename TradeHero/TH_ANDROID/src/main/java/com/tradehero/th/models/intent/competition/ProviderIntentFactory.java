package com.tradehero.th.models.intent.competition;

import android.content.Intent;
import com.tradehero.th.R;
import com.tradehero.th.models.intent.THIntentSubFactory;
import java.util.List;

/**
 * Created by xavier on 1/20/14.
 */
public class ProviderIntentFactory extends THIntentSubFactory<ProviderIntent>
{
    public static final String TAG = ProviderIntentFactory.class.getSimpleName();

    public ProviderIntentFactory()
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

    @Override protected ProviderIntent create(Intent intent, List<String> pathSegments)
    {
        String action = getAction(pathSegments);

        ProviderIntent providerIntent = null;

        // TODO

        //if (action.equals(getString(R.string.intent_action_portfolio_open)))
        //{
        //    providerIntent = new OpenProviderIntent(OneProviderIntent.getProviderId(pathSegments));
        //}

        return providerIntent;
    }
}
