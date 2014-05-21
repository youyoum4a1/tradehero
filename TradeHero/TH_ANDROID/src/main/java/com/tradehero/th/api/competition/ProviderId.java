package com.tradehero.th.api.competition;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class ProviderId extends AbstractIntegerDTOKey
{
    public final static String BUNDLE_KEY_KEY = ProviderId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public ProviderId(Integer key)
    {
        super(key);
    }

    public ProviderId(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[ProviderId key=%d]", key);
    }
}
