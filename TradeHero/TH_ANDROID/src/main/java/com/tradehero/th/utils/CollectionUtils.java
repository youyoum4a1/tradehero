package com.tradehero.th.utils;

import com.android.internal.util.Predicate;
import java.util.Collection;
import java.util.Iterator;

public class CollectionUtils
{
    public static <T> Collection<T> filter(Collection<T> c, Predicate<T> p)
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
