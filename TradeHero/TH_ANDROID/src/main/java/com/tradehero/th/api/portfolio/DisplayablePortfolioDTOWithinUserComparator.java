package com.tradehero.th.api.portfolio;

import java.util.Comparator;

/**
 * Created by xavier on 1/14/14.
 */
public class DisplayablePortfolioDTOWithinUserComparator implements Comparator<DisplayablePortfolioDTO>
{
    public static final String TAG = DisplayablePortfolioDTOWithinUserComparator.class.getSimpleName();

    private final PortfolioCompactDTODisplayComparator portfolioCompactDTODisplayComparator;

    public DisplayablePortfolioDTOWithinUserComparator()
    {
        this.portfolioCompactDTODisplayComparator = new PortfolioCompactDTODisplayComparator();
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
        return this.portfolioCompactDTODisplayComparator.compare(lhs.portfolioDTO, rhs.portfolioDTO);
    }
}
