package com.tradehero.th.api.market;

import java.util.ArrayList;
import java.util.Collection;

public class IndustryDTOList extends ArrayList<IndustryDTO>
{
    //<editor-fold desc="Constructors">
    public IndustryDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public IndustryDTOList()
    {
        super();
    }

    public IndustryDTOList(Collection<? extends IndustryDTO> c)
    {
        super(c);
    }
    //</editor-fold>
}
