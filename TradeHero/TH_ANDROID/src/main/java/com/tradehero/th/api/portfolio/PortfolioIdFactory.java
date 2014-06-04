package com.tradehero.th.api.portfolio;

import javax.inject.Inject;

public class PortfolioIdFactory
{
    //<editor-fold desc="Constructors">
    @Inject public PortfolioIdFactory()
    {
        super();
    }
    //</editor-fold>

    public PortfolioIdList createFrom(OwnedPortfolioIdList ownedPortfolioIds)
    {
        if (ownedPortfolioIds == null)
        {
            return null;
        }

        PortfolioIdList portfolioIds = new PortfolioIdList();
        for (OwnedPortfolioId ownedPortfolioId: ownedPortfolioIds)
        {
            portfolioIds.add(ownedPortfolioId.getPortfolioIdKey());
        }
        return portfolioIds;
    }
}
