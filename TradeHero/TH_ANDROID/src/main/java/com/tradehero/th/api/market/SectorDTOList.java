package com.ayondo.academy.api.market;

import android.support.annotation.NonNull;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.List;

public class SectorDTOList extends BaseArrayList<SectorDTO>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public SectorDTOList()
    {
        super();
    }
    //</editor-fold>

    @NonNull public List<SectorId> getSectorIds()
    {
        List<SectorId> list = new ArrayList<>();
        for (SectorCompactDTO compactDTO : this)
        {
            list.add(compactDTO.getSectorId());
        }
        return list;
    }
}
