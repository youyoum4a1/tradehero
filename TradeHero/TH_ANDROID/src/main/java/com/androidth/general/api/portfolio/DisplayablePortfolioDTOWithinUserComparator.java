package com.androidth.general.api.portfolio;

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

    @Override public int compare(@NonNull DisplayablePortfolioDTO lhs, @NonNull DisplayablePortfolioDTO rhs)
    {
        if(lhs instanceof LiveAccountPortfolioItemHeader || rhs instanceof LiveAccountPortfolioItemHeader){
            return 1;//make it first
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
