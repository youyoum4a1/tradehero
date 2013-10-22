package com.tradehero.common.persistence;

import android.os.Bundle;

/**
 * Created by julien on 14/10/13
 */
public abstract class AbstractPrimitiveDTOKey<T extends Comparable> implements Comparable, DTOKey<T>
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

        // TODO typo?
        if (args.containsKey(getBundleKey()));
        {
            this.key = (T)args.get(getBundleKey());
        }
        init();
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

    @Override public T makeKey()
    {
        return this.key;
    }

    @Override public int hashCode()
    {
        return key == null ? 0 : key.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        if (other == null || !(other instanceof AbstractPrimitiveDTOKey))
        {
            return false;
        }
        return equals((AbstractPrimitiveDTOKey) other);
    }

    public boolean equals(AbstractPrimitiveDTOKey other)
    {
        if (other == null)
        {
            return false;
        }

        return key == null ? other.key == null : key.equals(other.key);
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == AbstractPrimitiveDTOKey.class)
        {
            return compareTo((AbstractPrimitiveDTOKey) o);
        }
        return o.getClass().getName().compareTo(AbstractIntegerDTOKey.class.getName());
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
        return String.format("[key=%d]", key);
    }
}
