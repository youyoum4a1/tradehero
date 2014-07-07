package com.tradehero.common.api;

import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.Nullable;

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
        for (@Nullable T item : this)
        {
            if (item == null)
            {
                return true;
            }
        }
        return false;
    }
}
