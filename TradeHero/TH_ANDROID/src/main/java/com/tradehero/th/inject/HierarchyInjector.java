package com.tradehero.th.inject;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * In a family, parents have to raise their children, have to provide children what they need to grow,
 * this is the basic idea for creating HierarchyInjector.
 *
 *                                                       |--------------------|
 *                                                       |                    v
 * An Android family tree looks like: Application -> Activity -> Fragment -> |View     |
 *                                                                           |Adapter  |
 *
 * so that children are the subjects for its parents to inject.
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
        return false;
    }

    private static boolean inject(Context context, Object o)
    {
        return (context instanceof Injector) && inject((Injector) context, o);
    }

    private static boolean inject(Injector injector, Object o)
    {
        injector.inject(o);
        return true;
    }
}
