package com.tradehero.th.api.market;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractPrimitiveDTOKey;
import com.tradehero.common.persistence.AbstractStringDTOKey;

public final class SectorListType extends AbstractStringDTOKey
{
    public final static String BUNDLE_KEY_KEY = SectorListType.class.getName() + ".key";
    public final static String DEFAULT_KEY = "All";

    //<editor-fold desc="Constructors">
    public SectorListType()
    {
        super(DEFAULT_KEY);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override protected boolean equals(@NonNull AbstractPrimitiveDTOKey other)
    {
        return super.equals(other)
                && other instanceof SectorListType;
    }
}
