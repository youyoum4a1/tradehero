package com.tradehero.th.models.intent;

import android.content.Intent;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.models.intent.trending.TrendingIntentFactory;
import com.tradehero.th.utils.DaggerUtils;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/10/14.
 */
@Singleton public class THIntentFactory extends AbstractTHIntentFactory<THIntent>
{
    public static final String TAG = THIntentFactory.class.getSimpleName();

    @Inject TrendingIntentFactory trendingIntentFactory;
    // TODO other factories

    private Map<String, AbstractTHIntentFactory<? extends THIntent>> factoryMap;

    @Inject public THIntentFactory()
    {
        DaggerUtils.inject(this);
        factoryMap = new HashMap<>();
        factoryMap.put(getString(R.string.intent_host_trending), trendingIntentFactory);
        // TODO add factories
    }

    public THIntent create(Intent intent)
    {
        if (!isHandlableIntent(intent))
        {
            throw new IllegalArgumentException("Not a THIntent " + intent.getDataString());
        }
        String host = intent.getData().getHost();
        THIntent thIntent = null;
        if (factoryMap.containsKey(host))
        {
            thIntent = factoryMap.get(host).create(intent);
        }
        else
        {
            THLog.e(TAG, host + " host is unhandled " + intent.getDataString(), new Exception());
        }

        return thIntent;
    }
}
