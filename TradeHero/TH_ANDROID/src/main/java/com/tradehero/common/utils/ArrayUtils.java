package com.tradehero.common.utils;

import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArrayUtils
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
}
