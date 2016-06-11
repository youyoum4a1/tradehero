package com.androidth.general.inject;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.View;

/**
 * In a family, parents have to raise their children, have to provide children what they need to grow,
 * this is the basic idea for creating HierarchyInjector.
 *
 *                                                       |--------------------|
 *                                                       |                    v
 * An Android family tree looks like: Application --> Activity --> Fragment --> |View     |
 *                                                                              |Adapter  |
 *                                                                              |Loader   | ...
 *
 * so that children are the subjects for their parents to inject.
 */
public class HierarchyInjector
{
    private HierarchyInjector() { }

    public static boolean inject(Object o)
    {
        if (o instanceof View)
        {
            Context context = ((View) o).getContext();
            return inject(context, o) || inject(context.getApplicationContext(), o);
        }

        if (o instanceof Fragment)
        {
            Activity activity = ((Fragment) o).getActivity();
            return inject(activity, o) || inject(activity.getApplicationContext(), o);
        }

        if (o instanceof android.app.Fragment)
        {
            Activity activity = ((android.app.Fragment) o).getActivity();
            return inject(activity, o) || inject(activity.getApplicationContext(), o);
        }

        if (o instanceof Activity)
        {
            Activity activity = (Activity) o;
            return inject(activity, o) || inject(activity.getApplicationContext(), o);
        }

        throw new IllegalArgumentException("Hierarchy can only auto-inject (single argument) View, Fragment or Activity, not " + o.getClass().getSimpleName());
    }

    public static boolean inject(Context context, Object o)
    {
        // TODO Following check is used when view is created from a ContextThemeWrapper, instead of an Activity, which is wrapped inside the wrapper
        if (!(context instanceof Activity) && (context instanceof ContextThemeWrapper))
        {
            context = ((ContextThemeWrapper) context).getBaseContext();
        }
        else if (!(context instanceof Activity) && context instanceof android.support.v7.view.ContextThemeWrapper)
        {
//            context = ((android.support.v7.internal.view.ContextThemeWrapper) context).getBaseContext();
            context = ((android.support.v7.view.ContextThemeWrapper) context).getBaseContext();
        }

        return (context instanceof Injector) && inject((Injector) context, o);
    }

    private static boolean inject(Injector injector, Object o)
    {
        injector.inject(o);
        return true;
    }
}
