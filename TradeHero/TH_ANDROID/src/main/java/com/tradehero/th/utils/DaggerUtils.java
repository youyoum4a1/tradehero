package com.tradehero.th.utils;

import com.tradehero.th.base.Application;
import dagger.ObjectGraph;

/** Created with IntelliJ IDEA. User: tho Date: 9/16/13 Time: 5:18 PM Copyright (c) TradeHero */
public class DaggerUtils
{
    private static ObjectGraph objectGraph;

    public static void initialize(Application app)
    {
        objectGraph = ObjectGraph.create(getModules(app));
        objectGraph.injectStatics();
    }

    private static Object[] getModules(Application app)
    {
        return new Object[]
                {
                        new com.tradehero.th.utils.dagger.TradeHeroModule(app),
                        new com.tradehero.th.models.intent.IntentDaggerModule(),
                        new com.tradehero.th.fragments.competition.CompetitionModule(),
                        new com.tradehero.th.filter.FilterModule(),
                        new com.tradehero.th.models.graphics.TransformationModule(),
                        new com.tradehero.th.DebugModule(),
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
