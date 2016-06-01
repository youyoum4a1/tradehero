package com.ayondo.academy.models.market;

import com.tradehero.common.persistence.DTOKey;

public class ExchangeSectorKey implements DTOKey
{
    //<editor-fold desc="Constructors">
    public ExchangeSectorKey()
    {
        super();
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return 0;
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof ExchangeSectorKey;
    }
}
