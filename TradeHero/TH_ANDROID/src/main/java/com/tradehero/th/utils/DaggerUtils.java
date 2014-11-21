package com.tradehero.th.utils;

import android.support.annotation.NonNull;
import dagger.ObjectGraph;

/**
 * This class is deprecated, please use HierarchyInjector instead
 */
@Deprecated
public class DaggerUtils
{
    private static ObjectGraph objectGraph;

    public static void inject(Object object)
    {
        if (objectGraph != null && object != null)
        {
            objectGraph.inject(object);
        }
    }

    public static Object getObject(@NonNull Class<?> aClass)
    {
        return objectGraph.get(aClass);
    }

    public static void setObjectGraph(ObjectGraph objectGraph)
    {
        DaggerUtils.objectGraph = objectGraph;
    }
}
