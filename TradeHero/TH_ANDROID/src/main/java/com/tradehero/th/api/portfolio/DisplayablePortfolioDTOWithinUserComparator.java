package com.tradehero.th.api.portfolio;

import android.support.annotation.NonNull;
import java.util.Comparator;

public class DisplayablePortfolioDTOWithinUserComparator implements Comparator<DisplayablePortfolioDTO>
{
    @NonNull private final PortfolioCompactDTODisplayComparator portfolioCompactDTODisplayComparator;
    @NonNull private final OwnedPortfolioIdDisplayComparator ownedPortfolioIdDisplayComparator;

    //<editor-fold desc="Constructors">
    public DisplayablePortfolioDTOWithinUserComparator()
    {
        this.portfolioCompactDTODisplayComparator = new PortfolioCompactDTODisplayComparator();
        this.ownedPortfolioIdDisplayComparator = new OwnedPortfolioIdDisplayComparator();
    }
    //</editor-fold>

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
        if (lhs.portfolioDTO != null && rhs.portfolioDTO != null)
        {
            int portfolioComp = this.portfolioCompactDTODisplayComparator.compare(lhs.portfolioDTO, rhs.portfolioDTO);
            if (portfolioComp != 0)
            {
                return portfolioComp;
            }
        }
        return this.ownedPortfolioIdDisplayComparator.compare(lhs.ownedPortfolioId, rhs.ownedPortfolioId);
    }
}
