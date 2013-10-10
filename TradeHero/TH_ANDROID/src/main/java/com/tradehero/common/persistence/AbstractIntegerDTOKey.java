package com.tradehero.common.persistence;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 10/10/13 Time: 5:13 PM To change this template use File | Settings | File Templates. */
abstract public class AbstractIntegerDTOKey implements Comparable, DTOKey<Integer>
{
    public final Integer key;

    //<editor-fold desc="Constructors">
    public AbstractIntegerDTOKey(Integer key)
    {
        super();
        this.key = key;
        init();
    }

    public AbstractIntegerDTOKey(Bundle args)
    {
        super();
        if (args.containsKey(getBundleKey()));
        {
            this.key = args.getInt(getBundleKey());
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

    @Override public Integer makeKey()
    {
        return this.key;
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == AbstractIntegerDTOKey.class)
        {
            return compareTo((AbstractIntegerDTOKey) o);
        }
        return o.getClass().getName().compareTo(AbstractIntegerDTOKey.class.getName());
    }

    public int compareTo(AbstractIntegerDTOKey other)
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

    public void putParameters(Bundle args)
    {
        args.putInt(getBundleKey(), key);
    }

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
