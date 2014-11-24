package com.tradehero.th.api.market;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class SectorId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY_KEY = SectorId.class.getName() + ".key";
    //<editor-fold desc="Constructors">
    public SectorId(@NonNull Integer key)
    {
        super(key);
    }

    public SectorId(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
