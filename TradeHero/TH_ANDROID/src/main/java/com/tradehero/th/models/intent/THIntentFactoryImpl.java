package com.tradehero.th.models.intent;

import android.content.Intent;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.utils.route.THRouter;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class THIntentFactoryImpl extends THIntentFactory<THIntent>
{
    private final Map<String, THIntentFactory<? extends THIntent>> factoryMap;

    private final THRouter thRouter;
    private final CurrentActivityHolder currentActivityHolder;

    //<editor-fold desc="Constructors">
    @Inject public THIntentFactoryImpl(THRouter thRouter, CurrentActivityHolder currentActivityHolder)
    {
        this.thRouter = thRouter;
        this.currentActivityHolder = currentActivityHolder;
        factoryMap = new HashMap<>();
    }
    //</editor-fold>

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
            try
            {
                thIntent = factoryMap.get(host).create(intent);
            }
            catch (Exception ex)
            {
                handleUrlByRouter(intent, host);
                Timber.d("Something wrong with old THIntent" + ex.getMessage());
            }
        }
        if (thIntent == null)
        {
            handleUrlByRouter(intent, host);
        }

        return thIntent;
    }

    private void handleUrlByRouter(Intent intent, String host)
    {
        // open with thRouter when url is not handlable by THIntent

        // remove the protocol
        String url = intent.getDataString();
        Timber.d("Handling: %s", url);
        url = url.substring("tradehero://".length());

        // ignore query for now, TODO handle deeplink query
        int queryMark = url.indexOf('?');
        if (queryMark > 0)
        {
            url = url.substring(0, queryMark);
        }

        try
        {
            thRouter.open(url, currentActivityHolder.getCurrentActivity());
        }
        catch (Exception ex)
        {
            Timber.e(ex, "%s host is unhandled %s", host, intent.getDataString());
        }
    }
}
