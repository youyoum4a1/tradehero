package com.tradehero.common.utils;

import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import rx.functions.Action1;

public class CollectionUtils
{
    public static <T> List<T> filter(Collection<T> target, Predicate<T> predicate)
    {
        List<T> result = new ArrayList<>();
        for (T element: target)
        {
            if (predicate.apply(element))
            {
                result.add(element);
            }
        }
        return result;
    }

    public static <T> Collection<T> inPlaceFilter(Collection<T> c, Predicate<T> p)
    {
        Iterator<T> it = c.iterator();
        while (it.hasNext())
        {
            if (!p.apply(it.next()))
            {
                it.remove();
            }
        }
        return c;
    }

    public static <T> void apply(Collection<T> collection, Action1<T> action)
    {
        for (T element: collection)
        {
            action.call(element);
        }
    }

    public static <T> void apply(T[] collection, Action1<T> action)
    {
        for (T element: collection)
        {
            action.call(element);
        }
    }

    public static <T> T first(Collection<T> collection, Predicate<T> predicate)
    {
        for (T element: collection)
        {
            if (predicate.apply(element))
            {
                return element;
            }
        }
        return null;
    }
}
