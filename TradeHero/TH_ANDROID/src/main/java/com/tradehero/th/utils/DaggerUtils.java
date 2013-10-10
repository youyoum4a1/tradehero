package com.tradehero.th.utils;

import com.tradehero.th.base.Application;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.YahooEngine;
import com.tradehero.th.utils.dagger.TradeHeroModule;
import dagger.ObjectGraph;
import java.util.Arrays;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/16/13 Time: 5:18 PM Copyright (c) TradeHero */
public class DaggerUtils
{
    private static ObjectGraph objectGraph;

    public static void initialize()
    {
        objectGraph = ObjectGraph.create(new TradeHeroModule(NetworkEngine.getInstance(),  YahooEngine.getInstance(),
                Application.context()));
        objectGraph.injectStatics();
    }

    private static List<Object> getModules()
    {
        return Arrays.asList(
                );
    }

    public static void inject(Object object)
    {
        objectGraph.inject(object);
    }
}
