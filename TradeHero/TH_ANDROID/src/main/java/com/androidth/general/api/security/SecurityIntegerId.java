package com.androidth.general.api.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;

public class SecurityIntegerId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = SecurityIntegerId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public SecurityIntegerId(Bundle args)
    {
        super(args);
    }

    public SecurityIntegerId(Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
