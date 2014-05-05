package com.tradehero.th.utils;

import com.tradehero.th.base.Application;
import com.tradehero.th.filter.FilterModule;
import com.tradehero.th.fragments.competition.CompetitionModule;
import com.tradehero.th.models.intent.IntentDaggerModule;
import com.tradehero.th.utils.dagger.TradeHeroModule;
import dagger.ObjectGraph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
        Object[] modules = new Object[]
                {
                        new TradeHeroModule(app),
                        new IntentDaggerModule(),
                        new CompetitionModule(),
                        new FilterModule()
                };

        if (!Constants.RELEASE)
        {
            List<Object> listModules = new ArrayList<>(Arrays.asList(modules));
            //listModules.add(new DebugModule());
            return listModules.toArray();
        }
        return modules;
    }

    public static void inject(Object object)
    {
        if (objectGraph != null && object != null)
        {
            objectGraph.inject(object);
        }
    }
}
