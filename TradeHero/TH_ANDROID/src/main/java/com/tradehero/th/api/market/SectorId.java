package com.tradehero.th.api.market;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import org.jetbrains.annotations.NotNull;

public class SectorId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY_KEY = SectorId.class.getName() + ".key";
    //<editor-fold desc="Constructors">
    public SectorId(@NotNull Integer key)
    {
        super(key);
    }

    public SectorId(@NotNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
