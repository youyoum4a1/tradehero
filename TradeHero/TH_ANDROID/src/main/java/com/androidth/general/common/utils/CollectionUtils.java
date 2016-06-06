package com.androidth.general.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import rx.functions.Action1;
import rx.functions.Func1;

public class CollectionUtils
{
    @NonNull public static <T> List<T> filter(@NonNull Iterable<T> target, @NonNull Predicate<T> predicate)
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

    @NonNull public static <T> Collection<T> inPlaceFilter(@NonNull Collection<T> c, @NonNull Predicate<T> p)
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

    public static <T> void apply(@NonNull Iterable<T> collection, @NonNull Action1<T> action)
    {
        for (T element: collection)
        {
            action.call(element);
        }
    }

    public static <T> void apply(@NonNull T[] collection, @NonNull Action1<T> action)
    {
        for (T element: collection)
        {
            action.call(element);
        }
    }

    @Nullable public static <T> T first(@NonNull Iterable<T> collection, @NonNull Predicate<T> predicate)
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

    public static <T> boolean contains(@NonNull Iterable<T> collection, @NonNull Predicate<T> predicate)
    {
        for (T element: collection)
        {
            if (predicate.apply(element))
            {
                return true;
            }
        }
        return false;
    }

    @NonNull public static <S, T> List<S> map(@NonNull Iterable<T> collection, @NonNull Func1<T, S> mapper)
    {
        List<S> created = new ArrayList<>();
        for (T element : collection)
        {
            created.add(mapper.call(element));
        }
        return created;
    }

    public static <T> int count(@NonNull Iterable<T> collection, @NonNull Predicate<T> predicate)
    {
        int count = 0;
        for (T element: collection)
        {
            if (predicate.apply(element))
            {
                count++;
            }
        }
        return count;
    }
}
