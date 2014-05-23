package com.tradehero.th.api.competition.key;

import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class ProviderListKey extends AbstractIntegerDTOKey
{
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

    @Override public String toString()
    {
        return String.format("[ProviderListKey key=%d]", key);
    }
}
