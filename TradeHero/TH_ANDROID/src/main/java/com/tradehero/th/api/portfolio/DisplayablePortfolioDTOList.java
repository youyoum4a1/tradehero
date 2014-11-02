package com.tradehero.th.api.portfolio;

import com.tradehero.common.api.BaseArrayList;
import java.util.Collection;

public class DisplayablePortfolioDTOList extends BaseArrayList<DisplayablePortfolioDTO>
{
    //<editor-fold desc="Constructors">
    public DisplayablePortfolioDTOList()
    {
        super();
    }

    public DisplayablePortfolioDTOList(Collection<? extends DisplayablePortfolioDTO> c)
    {
        super(c);
    }
    //</editor-fold>
}
