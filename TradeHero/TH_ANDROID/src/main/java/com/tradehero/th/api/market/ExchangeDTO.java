package com.ayondo.academy.api.market;

import android.support.annotation.Nullable;

public class ExchangeDTO extends ExchangeCompactDTO
{
    @Nullable public SectorDTOList sectors;

    //<editor-fold desc="Constructors">
    protected ExchangeDTO()
    {
        super();
    }
    //</editor-fold>
}
