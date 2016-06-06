package com.androidth.general.api.competition.key;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;

public class ProviderListKey extends AbstractIntegerDTOKey
{
    public final static String BUNDLE_KEY_KEY = ProviderListKey.class.getName() + ".key";
    public static final Integer ALL_PROVIDERS = 0;

    //<editor-fold desc="Constructor">
    public ProviderListKey()
    {
        super(ALL_PROVIDERS);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[ProviderListKey key=%d]", key);
    }
}
