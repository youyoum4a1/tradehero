package com.tradehero.th.api.security;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;


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

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
