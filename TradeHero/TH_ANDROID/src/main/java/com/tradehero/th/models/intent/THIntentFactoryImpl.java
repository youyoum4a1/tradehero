package com.tradehero.th.models.intent;

import android.content.Intent;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by xavier on 1/10/14.
 */
public class THIntentFactoryImpl extends THIntentFactory<THIntent>
{
    private Map<String, THIntentFactory<? extends THIntent>> factoryMap;

    @Inject public THIntentFactoryImpl()
    {
        factoryMap = new HashMap<>();
    }

    @Override public String getHost()
    {
        throw new RuntimeException();
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
            Timber.e("%s host is unhandled %s", host, intent.getDataString(), new Exception());
        }

        return thIntent;
    }
}
