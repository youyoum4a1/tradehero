package com.tradehero.th.models.intent.trending;

import android.content.Intent;
import com.tradehero.th.R;
import com.tradehero.th.models.intent.AbstractTHIntentFactory;
import com.tradehero.th.models.intent.security.SecurityBuyIntent;
import com.tradehero.th.models.intent.security.SecuritySellIntent;
import com.tradehero.th.models.intent.security.SecurityTradeIntent;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/10/14.
 */
@Singleton public class TrendingIntentFactory extends AbstractTHIntentFactory<TrendingIntent>
{
    public static final String TAG = TrendingIntentFactory.class.getSimpleName();

    @Inject public TrendingIntentFactory()
    {
    }

    @Override public boolean isHandlableIntent(Intent intent)
    {
        return super.isHandlableIntent(intent) &&
            isHandlableHost(intent.getData().getHost());
    }

    public boolean isHandlableHost(String host)
    {
        return getString(R.string.intent_host_trending).equals(host);
    }

    @Override public TrendingIntent create(Intent intent)
    {
        if (!isHandlableIntent(intent))
        {
            throw new IllegalArgumentException("Not a TrendingIntent " + intent.getDataString());
        }
        List<String> pathSegments = intent.getData().getPathSegments();
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
