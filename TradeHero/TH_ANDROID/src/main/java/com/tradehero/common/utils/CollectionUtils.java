package com.tradehero.common.utils;

import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
}
