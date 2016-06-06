package com.androidth.general.common.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.Collection;

public class BaseArrayList<T> extends ArrayList<T>
{
    //<editor-fold desc="Constructors">

    public BaseArrayList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public BaseArrayList()
    {
        super();
    }

    public BaseArrayList(Collection<? extends T> c)
    {
        super(c);
    }
    //</editor-fold>

    public boolean hasNullItem()
    {
        for (T item : this)
        {
            if (item == null)
            {
                return true;
            }
        }
        return false;
    }

    @Nullable public T findFirstWhere(@NonNull Predicate<T> predicate)
    {
        for (T item : this)
        {
            if (predicate.apply(item))
            {
                return item;
            }
        }
        return null;
    }

    @NonNull public BaseArrayList<T> findWhere(@NonNull Predicate<T> predicate)
    {
        BaseArrayList<T> found = new BaseArrayList<>();
        for (T item : this)
        {
            if (predicate.apply(item))
            {
                found.add(item);
            }
        }
        return found;
    }

    public StringBuilder createStringBuilder()
    {
        StringBuilder sb = new StringBuilder("[");
        String separator = "";
        for (T item : this)
        {
            sb.append(separator).append(item);
            separator = ", ";
        }
        sb.append("]");
        return sb;
    }
}
