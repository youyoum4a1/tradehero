package com.tradehero.th.models.market;

import com.tradehero.common.persistence.DTOKey;

public class ExchangeSectorCompactKey implements DTOKey
{
    //<editor-fold desc="Constructors">
    public ExchangeSectorCompactKey()
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
        return other instanceof ExchangeSectorCompactKey;
    }
}
