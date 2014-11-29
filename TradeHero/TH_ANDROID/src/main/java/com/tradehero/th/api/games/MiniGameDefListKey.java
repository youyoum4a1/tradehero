package com.tradehero.th.api.games;

import com.tradehero.common.persistence.DTOKey;

public class MiniGameDefListKey implements DTOKey
{
    //<editor-fold desc="Constructors">
    public MiniGameDefListKey()
    {
    }
    //</editor-fold>

    @Override public boolean equals(Object other)
    {
        return other instanceof MiniGameDefListKey;
    }

    @Override public int hashCode()
    {
        return 0;
    }
}
