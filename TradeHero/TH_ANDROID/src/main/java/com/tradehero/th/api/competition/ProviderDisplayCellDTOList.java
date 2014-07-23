package com.tradehero.th.api.competition;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;

public class ProviderDisplayCellDTOList extends BaseArrayList<ProviderDisplayCellDTO>
        implements DTO
{
    public ProviderDisplayCellDTOList()
    {
        super();
    }

    public ProviderDisplayCellIdList createKeys()
    {
        ProviderDisplayCellIdList list = new ProviderDisplayCellIdList();
        for (ProviderDisplayCellDTO providerDisplayCellDTO : this)
        {
            list.add(providerDisplayCellDTO.getProviderDisplayCellId());
        }
        return list;
    }
}
