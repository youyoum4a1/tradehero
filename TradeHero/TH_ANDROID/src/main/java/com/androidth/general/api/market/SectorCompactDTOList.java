package com.androidth.general.api.market;

import android.support.annotation.NonNull;
import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
