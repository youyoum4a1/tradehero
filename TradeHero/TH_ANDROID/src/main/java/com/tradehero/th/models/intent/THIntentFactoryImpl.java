package com.tradehero.th.models.intent;

import android.content.Intent;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.models.intent.portfolio.PortfolioIntentFactory;
import com.tradehero.th.models.intent.trending.TrendingIntentFactory;
import com.tradehero.th.utils.DaggerUtils;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by xavier on 1/10/14.
 */
public class THIntentFactoryImpl extends THIntentFactory<THIntent>
{
    public static final String TAG = THIntentFactoryImpl.class.getSimpleName();

    private Map<String, THIntentFactory<? extends THIntent>> factoryMap;

    public THIntentFactoryImpl()
    {
        factoryMap = new HashMap<>();
    }

    @Override public String getHost()
    {
        throw new NotImplementedException();
    }

    public <T extends THIntent> void addSubFactory(THIntentFactory<T> factory)
    {
        factoryMap.put(factory.getHost(), factory);
    }

    public void clear()
    {
        factoryMap.clear();
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
