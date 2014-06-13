package com.tradehero.th.api.portfolio;

import java.util.ArrayList;
import java.util.Collection;

public class PortfolioIdList extends ArrayList<PortfolioId>
{
    //<editor-fold desc="Description">
    public PortfolioIdList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public PortfolioIdList()
    {
        super();
    }

    public PortfolioIdList(Collection<? extends PortfolioId> c)
    {
        super(c);
    }
    //</editor-fold>
}
