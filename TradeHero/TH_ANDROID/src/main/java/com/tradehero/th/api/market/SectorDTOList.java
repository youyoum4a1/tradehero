package com.tradehero.th.api.market;

import java.util.ArrayList;
import java.util.Collection;

public class SectorDTOList extends ArrayList<SectorDTO>
{
    //<editor-fold desc="Constructors">
    public SectorDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public SectorDTOList()
    {
        super();
    }

    public SectorDTOList(Collection<? extends SectorDTO> c)
    {
        super(c);
    }
    //</editor-fold>
}
