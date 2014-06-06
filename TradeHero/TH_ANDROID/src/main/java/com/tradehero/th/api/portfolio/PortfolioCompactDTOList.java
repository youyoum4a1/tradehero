package com.tradehero.th.api.portfolio;

import java.util.ArrayList;
import java.util.Collection;

public class PortfolioCompactDTOList extends ArrayList<PortfolioCompactDTO>
{
    //<editor-fold desc="Constructors">
    public PortfolioCompactDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public PortfolioCompactDTOList()
    {
        super();
    }

    public PortfolioCompactDTOList(Collection<? extends PortfolioCompactDTO> c)
    {
        super(c);
    }
    //</editor-fold>
}
