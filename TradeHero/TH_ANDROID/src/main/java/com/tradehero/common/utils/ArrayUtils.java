package com.tradehero.common.utils;

import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 4:47 PM To change this template use File | Settings | File Templates. */
public class ArrayUtils
{
    public static final String TAG = ArrayUtils.class.getSimpleName();

    public static <T> List<T> filter(Collection<T> target, Predicate<T> predicate)
    {
        List<T> result = new ArrayList<T>();
        for (T element: target)
        {
            if (predicate.apply(element))
            {
                result.add(element);
            }
        }
        return result;
    }
}
