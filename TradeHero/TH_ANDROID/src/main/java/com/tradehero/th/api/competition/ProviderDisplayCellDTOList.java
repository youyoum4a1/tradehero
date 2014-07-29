package com.tradehero.th.api.competition;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.BaseArrayListHasExpiration;
import com.tradehero.common.persistence.DTO;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

public class ProviderDisplayCellDTOList extends BaseArrayListHasExpiration<ProviderDisplayCellDTO>
        implements DTO
{
    private static final int DEFAULT_LIFE_EXPECTANCY = 300;

    public ProviderDisplayCellDTOList()
    {
        super(DEFAULT_LIFE_EXPECTANCY);
    }

    public ProviderDisplayCellDTOList(@NotNull Date expirationDate)
    {
        super(expirationDate);
    }

    @NotNull public ProviderDisplayCellIdList createKeys()
    {
        ProviderDisplayCellIdList list = new ProviderDisplayCellIdList(expirationDate);
        for (ProviderDisplayCellDTO providerDisplayCellDTO : this)
        {
            list.add(providerDisplayCellDTO.getProviderDisplayCellId());
        }
        return list;
    }
}
