package com.tradehero.th.models.intent.portfolio;

import android.content.Intent;
import com.tradehero.th.R;
import com.tradehero.th.models.intent.THIntentSubFactory;
import com.tradehero.th.models.intent.position.OnePortfolioIntent;
import com.tradehero.th.models.intent.position.OpenPortfolioIntent;
import java.util.List;
import javax.inject.Inject;

public class PortfolioIntentFactory extends THIntentSubFactory<PortfolioIntent>
{
    @Inject public PortfolioIntentFactory()
    {
    }

    @Override public String getHost()
    {
        return getString(R.string.intent_host_portfolio);
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

    @Override protected PortfolioIntent create(Intent intent, List<String> pathSegments)
    {
        String action = getAction(pathSegments);

        PortfolioIntent portfolioIntent = null;

        if (action.equals(getString(R.string.intent_action_portfolio_open)))
        {
            portfolioIntent = new OpenPortfolioIntent(OnePortfolioIntent.getPortfolioId(pathSegments));
        }

        return portfolioIntent;
    }
}
