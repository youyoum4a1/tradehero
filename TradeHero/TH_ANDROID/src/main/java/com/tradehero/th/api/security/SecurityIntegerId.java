package com.tradehero.th.api.security;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 12:35 PM To change this template use File | Settings | File Templates. */
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
