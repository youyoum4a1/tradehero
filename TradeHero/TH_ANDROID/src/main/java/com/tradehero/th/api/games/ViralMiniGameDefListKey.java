package com.tradehero.th.api.games;

import com.tradehero.common.persistence.DTOKey;

public class ViralMiniGameDefListKey implements DTOKey
{
    //<editor-fold desc="Constructors">
    public ViralMiniGameDefListKey()
    {
    }
    //</editor-fold>

    @Override public boolean equals(Object other)
    {
        return other instanceof ViralMiniGameDefListKey;
    }

    @Override public int hashCode()
    {
        return 0;
    }
}
