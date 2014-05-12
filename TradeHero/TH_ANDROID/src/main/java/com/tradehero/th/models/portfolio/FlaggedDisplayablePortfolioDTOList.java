package com.tradehero.th.models.portfolio;

import java.util.ArrayList;
import java.util.Collection;

class FlaggedDisplayablePortfolioDTOList extends ArrayList<FlaggedDisplayablePortfolioDTO>
{
    public boolean fetchingIds = false;

    //<editor-fold desc="Constructors">
    public FlaggedDisplayablePortfolioDTOList(int capacity)
    {
        super(capacity);
    }

    public FlaggedDisplayablePortfolioDTOList()
    {
        super();
    }

    public FlaggedDisplayablePortfolioDTOList(Collection<? extends FlaggedDisplayablePortfolioDTO> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
