package com.tradehero.th.api.market;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import java.util.Collection;

public class SectorCompactDTOList extends BaseArrayList<SectorCompactDTO> implements DTO
{
    //<editor-fold desc="Constructors">
    public SectorCompactDTOList()
    {
        super();
    }

    public SectorCompactDTOList(Collection<? extends SectorCompactDTO> c)
    {
        super(c);
    }
    //</editor-fold>
}
