package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.security.SecurityListType;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:07 PM To change this template use File | Settings | File Templates. */
public class ProviderListKey implements Comparable<ProviderListKey>, DTOKey<Integer>
{
    public static final String TAG = ProviderListKey.class.getSimpleName();

    public static final Integer ALL_PROVIDERS = 0;

    private final Integer key;

    //<editor-fold desc="Constructor">
    public ProviderListKey()
    {
        this(ALL_PROVIDERS);
    }

    public ProviderListKey(Integer key)
    {
        this.key = key;
        init();
    }
    //</editor-fold>

    private void init()
    {
        if (this.key == null)
        {
            throw new NullPointerException("Key cannot be null");
        }
    }

    //<editor-fold desc="Accessors">
    public Integer getKey()
    {
        return key;
    }
    //</editor-fold>

    @Override public int compareTo(ProviderListKey providerListKey)
    {
        if (providerListKey == null)
        {
            return 1;
        }

        return key.compareTo(providerListKey.key);
    }

    @Override public Integer makeKey()
    {
        return key;
    }

    @Override public String toString()
    {
        return String.format("[%s key=%d]", TAG, key);
    }
}
