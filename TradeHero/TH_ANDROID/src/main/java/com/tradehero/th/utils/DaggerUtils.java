package com.tradehero.th.utils;

import android.support.annotation.NonNull;

/**
 * This class is deprecated, please use HierarchyInjector instead
 */
@Deprecated
public class DaggerUtils
{
    public static void inject(Object object)
    {
        // FIXME dagger2
    }

    public static Object getObject(@NonNull Class<?> aClass)
    {
        return null;
        // FIXME dagger2
        //return objectGraph.get(aClass);
    }
}
