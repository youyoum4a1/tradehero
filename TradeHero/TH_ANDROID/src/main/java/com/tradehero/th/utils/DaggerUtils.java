package com.tradehero.th.utils;

import dagger.ObjectGraph;

public class DaggerUtils
{
    private static ObjectGraph objectGraph;

    public static void initialize(Object[] modules)
    {
        objectGraph = ObjectGraph.create(modules);
        objectGraph.injectStatics();
    }

    public static void inject(Object object)
    {
        if (objectGraph != null && object != null)
        {
            objectGraph.inject(object);
        }
    }
}
