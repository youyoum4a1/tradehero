package com.androidth.general.api.live.key;

import android.support.annotation.NonNull;

import com.androidth.general.common.persistence.AbstractIntegerDTOKey;

public class SecurityTypeKey extends AbstractIntegerDTOKey
{
    public final static String BUNDLE_KEY_KEY = SecurityTypeKey.class.getName() + ".key";
    public static final Integer DEFAULT_SECURITY_TYPE_KEY = 0;

    //<editor-fold desc="Constructor">
    public SecurityTypeKey()
    {
        super(DEFAULT_SECURITY_TYPE_KEY);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[SecurityTypeKey key=%d]", key);
    }
}
