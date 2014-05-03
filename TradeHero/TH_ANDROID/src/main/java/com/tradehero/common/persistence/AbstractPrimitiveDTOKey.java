package com.tradehero.common.persistence;

import android.os.Bundle;


public abstract class AbstractPrimitiveDTOKey<T extends Comparable> implements Comparable, DTOKey
{
    public final T key;

    //<editor-fold desc="Constructors">
    public AbstractPrimitiveDTOKey(T key)
    {
        super();
        this.key = key;
        init();
    }

    public AbstractPrimitiveDTOKey(Bundle args)
    {
        super();

        if (args.containsKey(getBundleKey()))
        {
            key = fromKeyBundle(args.get(getBundleKey()));
        }
        else
        {
            key = null;
        }
        init();
    }

    protected T fromKeyBundle(Object keyBundle)
    {
        return (T) keyBundle;
    }
    //</editor-fold>

    protected void init()
    {
        if (this.key == null)
        {
            throw new NullPointerException("Key cannot be null");
        }
    }

    abstract public String getBundleKey();

    @Override public int hashCode()
    {
        return key == null ? 0 : key.hashCode();
    }

    //@Override public boolean equals(Object other)
    //{
    //    if (other == null || !(other instanceof AbstractPrimitiveDTOKey))
    //    {
    //        return false;
    //    }
    //    return equals((AbstractPrimitiveDTOKey) other);
    //}

    @Override public boolean equals(Object other)
    {
        return other != null &&
        getClass().isInstance(other) &&
        other.getClass().isInstance(this) &&
        equals(getClass().cast(other));
    }

    public boolean equals(AbstractPrimitiveDTOKey other)
    {
        return (other != null) &&
                getClass().isInstance(other) &&
                other.getClass().isInstance(this) &&
                (key == null ? other.key == null : key.equals(other.key));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == getClass())
        {
            return compareTo(getClass().cast(o));
        }
        return o.getClass().getName().compareTo(getClass().getName());
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

    public abstract void putParameters(Bundle args);

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @Override public String toString()
    {
        return String.format("[key=%s]", key);
    }
}
