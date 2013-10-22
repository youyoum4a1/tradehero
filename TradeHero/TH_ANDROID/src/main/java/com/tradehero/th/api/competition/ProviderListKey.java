package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:07 PM To change this template use File | Settings | File Templates. */
public class ProviderListKey extends AbstractIntegerDTOKey
{
    public static final String TAG = ProviderListKey.class.getSimpleName();
    public final static String BUNDLE_KEY_KEY = ProviderListKey.class.getName() + ".key";
    public static final Integer ALL_PROVIDERS = 0;

    //<editor-fold desc="Constructor">
    public ProviderListKey()
    {
        this(ALL_PROVIDERS);
    }

    public ProviderListKey(Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof ProviderListKey) && super.equals((ProviderListKey) other);
    }

    @Override public String toString()
    {
        return String.format("[%s key=%d]", TAG, key);
    }
}
