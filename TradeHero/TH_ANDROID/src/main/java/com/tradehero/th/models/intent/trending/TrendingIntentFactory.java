package com.tradehero.th.models.intent.trending;

import android.content.Intent;
import com.tradehero.th.R;
import com.tradehero.th.models.intent.THIntentSubFactory;
import com.tradehero.th.models.intent.security.SecurityBuyIntent;
import com.tradehero.th.models.intent.security.SecuritySellIntent;
import com.tradehero.th.models.intent.security.SecurityTradeIntent;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by xavier on 1/10/14.
 */
public class TrendingIntentFactory extends THIntentSubFactory<TrendingIntent>
{
    @Inject public TrendingIntentFactory()
    {
    }

    @Override public String getHost()
    {
        return getString(R.string.intent_host_trending);
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

    @Override protected TrendingIntent create(Intent intent, List<String> pathSegments)
    {
        String action = getAction(pathSegments);

        TrendingIntent trendingIntent = null;

        if (action.equals(getString(R.string.intent_action_security_buy)))
        {
            trendingIntent = new SecurityBuyIntent(SecurityTradeIntent.getSecurityId(pathSegments));
        }
        else if (action.equals(getString(R.string.intent_action_security_sell)))
        {
            trendingIntent = new SecuritySellIntent(SecurityTradeIntent.getSecurityId(pathSegments));
        }

        return trendingIntent;
    }
}
