package com.androidth.general.common.persistence;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class AbstractPrimitiveDTOKey<T extends Comparable> implements Comparable, DTOKey
{
    @NonNull public T key;

    //<editor-fold desc="Constructors">
    public AbstractPrimitiveDTOKey() {}

    public AbstractPrimitiveDTOKey(@NonNull T key)
    {
        super();
        this.key = key;
    }

    public AbstractPrimitiveDTOKey(@NonNull Bundle args)
    {
        super();
        key = fromKeyBundle(args.get(getBundleKey()));
    }

    protected T fromKeyBundle(Object keyBundle)
    {
        return (T) keyBundle;
    }
    //</editor-fold>

    @NonNull abstract public String getBundleKey();

    @Override public int hashCode()
    {
        return key.hashCode();
    }

    //@Override public boolean equals(Object other)
    //{
    //    if (other == null || !(other instanceof AbstractPrimitiveDTOKey))
    //    {
    //        return false;
    //    }
    //    return equals((AbstractPrimitiveDTOKey) other);
    //}

    @Override public boolean equals(@Nullable Object other)
    {
        return other != null &&
        getClass().isInstance(other) &&
        other.getClass().isInstance(this) &&
        equals(getClass().cast(other));
    }

    protected boolean equals(@NonNull AbstractPrimitiveDTOKey other)
    {
        return getClass().isInstance(other) &&
                other.getClass().isInstance(this) &&
                key.equals(other.key);
    }

    @Override public int compareTo(@NonNull Object other)
    {
        if (other.getClass() == getClass())
        {
            return compareTo(getClass().cast(other));
        }
        return other.getClass().getName().compareTo(getClass().getName());
    }

    public int compareTo(AbstractPrimitiveDTOKey other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        return key.compareTo(other.key);
    }

    public abstract void putParameters(@NonNull Bundle args);

    @NonNull public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @Override public String toString()
    {
        return String.format("[%s key=%s]", getClass(), key);
    }
}
