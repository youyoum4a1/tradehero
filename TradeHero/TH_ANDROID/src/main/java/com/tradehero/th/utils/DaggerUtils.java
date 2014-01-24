package com.tradehero.th.utils;

import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.th.base.Application;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.YahooEngine;
import dagger.ObjectGraph;

/** Created with IntelliJ IDEA. User: tho Date: 9/16/13 Time: 5:18 PM Copyright (c) TradeHero */
public class DaggerUtils
{
    private static ObjectGraph objectGraph;

    public static void initialize()
    {
        LruMemFileCache.createInstance(Application.context());
        objectGraph = ObjectGraph.create(getModules());
        objectGraph.injectStatics();
    }

    private static Object[] getModules()
    {
        return new Object[]
                {
                        new com.tradehero.th.utils.dagger.TradeHeroModule(
                                NetworkEngine.getInstance(),
                                YahooEngine.getInstance(),
                                Application.context(),
                                LruMemFileCache.getInstance()),
                        new com.tradehero.th.models.intent.IntentDaggerModule(),
                        new com.tradehero.th.fragments.competition.CompetitionModule(),
                        new com.tradehero.th.filter.FilterModule()
                };
    }

    public static void inject(Object object)
    {
        if (objectGraph != null && object != null)
        {
            objectGraph.inject(object);
        }
    }
}
