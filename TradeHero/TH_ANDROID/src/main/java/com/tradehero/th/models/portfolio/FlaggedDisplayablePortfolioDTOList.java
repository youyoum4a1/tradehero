package com.tradehero.th.models.portfolio;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by xavier on 3/5/14.
 */
class FlaggedDisplayablePortfolioDTOList extends ArrayList<FlaggedDisplayablePortfolioDTO>
{
    public static final String TAG = FlaggedDisplayablePortfolioDTOList.class.getSimpleName();

    public boolean fetchingIds = false;

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
}
