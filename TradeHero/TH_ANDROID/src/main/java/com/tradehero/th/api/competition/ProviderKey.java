package com.tradehero.th.api.competition;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/10/13 Time: 5:13 PM To change this template use File | Settings | File Templates. */
public class ProviderKey implements Comparable, DTOKey<Integer>
{
    public final static String BUNDLE_KEY_KEY = ProviderKey.class.getName() + ".key";

    public final Integer key;

    public ProviderKey(Integer key)
    {
        super();
        this.key = key;
        init();
    }

    public ProviderKey(Bundle args)
    {
        super();
        if (args.containsKey(BUNDLE_KEY_KEY));
        {
            this.key = args.getInt(BUNDLE_KEY_KEY);
        }
        init();
    }

    private void init()
    {
        if (this.key == null)
        {
            throw new NullPointerException("Key cannot be null");
        }
    }

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

        if (o.getClass() == ProviderKey.class)
        {
            return compareTo((ProviderKey) o);
        }
        return o.getClass().getName().compareTo(ProviderKey.class.getName());
    }

    public int compareTo(ProviderKey other)
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
        args.putInt(BUNDLE_KEY_KEY, key);
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
