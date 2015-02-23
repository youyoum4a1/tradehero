package com.tradehero.th.api.market;

import android.support.annotation.Nullable;

public class ExchangeDTO extends ExchangeCompactDTO implements WithTopSecurities
{
    @Nullable public SectorDTOList sectors;
    @Nullable public SecuritySuperCompactDTOList topSecurities;

    //<editor-fold desc="Constructors">
    protected ExchangeDTO()
    {
        super();
    }
    //</editor-fold>

    @Nullable @Override public SecuritySuperCompactDTOList getTopSecurities()
    {
        return topSecurities;
    }
}
