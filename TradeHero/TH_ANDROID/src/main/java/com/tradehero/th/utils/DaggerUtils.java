package com.tradehero.th.utils;

import com.tradehero.th.base.Application;
import com.tradehero.th.utils.dagger.ConverterModule;
import com.tradehero.th.utils.dagger.ManagerModule;
import com.tradehero.th.utils.dagger.NetworkModule;
import com.tradehero.th.utils.dagger.PersistenceModule;
import com.tradehero.th.utils.dagger.TextProcessorModule;
import dagger.ObjectGraph;
import java.util.Arrays;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/16/13 Time: 5:18 PM Copyright (c) TradeHero */
public class DaggerUtils
{
    private static ObjectGraph objectGraph;

    public static void initialize()
    {
        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.injectStatics();
    }

    public static List<Object> getModules()
    {
        return Arrays.asList(
                new PersistenceModule(Application.context()),
                new ManagerModule(),
                new ConverterModule(),
                new TextProcessorModule(),
                new NetworkModule()
        );
    }

    public static void inject(Object object)
    {
        objectGraph.inject(object);
    }
}
