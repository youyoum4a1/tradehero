package com.tradehero.th.api.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseArrayListHasExpiration;
import com.tradehero.common.persistence.DTO;
import java.util.Date;

public class ProviderDisplayCellDTOList extends BaseArrayListHasExpiration<ProviderDisplayCellDTO>
        implements DTO
{
    private static final int DEFAULT_LIFE_EXPECTANCY = 300;

    public ProviderDisplayCellDTOList()
    {
        super(DEFAULT_LIFE_EXPECTANCY);
    }

    public ProviderDisplayCellDTOList(@NonNull Date expirationDate)
    {
        super(expirationDate);
    }

    @NonNull public ProviderDisplayCellIdList createKeys()
    {
        ProviderDisplayCellIdList list = new ProviderDisplayCellIdList(expirationDate);
        for (ProviderDisplayCellDTO providerDisplayCellDTO : this)
        {
            list.add(providerDisplayCellDTO.getProviderDisplayCellId());
        }
        return list;
    }
}
