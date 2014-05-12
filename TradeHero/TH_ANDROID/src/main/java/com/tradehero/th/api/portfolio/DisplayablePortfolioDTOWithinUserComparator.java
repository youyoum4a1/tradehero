package com.tradehero.th.api.portfolio;

import java.util.Comparator;


public class DisplayablePortfolioDTOWithinUserComparator implements Comparator<DisplayablePortfolioDTO>
{
    public static final String TAG = DisplayablePortfolioDTOWithinUserComparator.class.getSimpleName();

    private final PortfolioCompactDTODisplayComparator portfolioCompactDTODisplayComparator;
    private final OwnedPortfolioIdDisplayComparator ownedPortfolioIdDisplayComparator;

    public DisplayablePortfolioDTOWithinUserComparator()
    {
        this.portfolioCompactDTODisplayComparator = new PortfolioCompactDTODisplayComparator();
        this.ownedPortfolioIdDisplayComparator = new OwnedPortfolioIdDisplayComparator();
    }

    @Override public int compare(DisplayablePortfolioDTO lhs, DisplayablePortfolioDTO rhs)
    {
        if (lhs == null)
        {
            return rhs == null ? 0 : 1;
        }
        else if (rhs == null)
        {
            return -1;
        }
        int portfolioComp = this.portfolioCompactDTODisplayComparator.compare(lhs.portfolioDTO, rhs.portfolioDTO);
        if (portfolioComp != 0)
        {
            return portfolioComp;
        }
        return this.ownedPortfolioIdDisplayComparator.compare(lhs.ownedPortfolioId, rhs.ownedPortfolioId);
    }
}
