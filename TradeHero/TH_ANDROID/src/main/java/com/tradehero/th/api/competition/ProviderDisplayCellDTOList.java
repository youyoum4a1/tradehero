package com.ayondo.academy.api.competition;

import com.tradehero.common.persistence.BaseArrayListHasExpiration;
import com.tradehero.common.persistence.DTO;

public class ProviderDisplayCellDTOList extends BaseArrayListHasExpiration<ProviderDisplayCellDTO>
        implements DTO
{
    private static final int DEFAULT_LIFE_EXPECTANCY = 300;

    //<editor-fold desc="Constructors">
    public ProviderDisplayCellDTOList()
    {
        super(DEFAULT_LIFE_EXPECTANCY);
    }
    //</editor-fold>
}
