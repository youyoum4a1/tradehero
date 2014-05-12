package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseDTO;

class FlaggedDisplayablePortfolioDTO extends DisplayablePortfolioDTO
{
    public boolean fetchingUser = false;
    public boolean fetchingPortfolio = false;

    //<editor-fold desc="Constructors">
    public FlaggedDisplayablePortfolioDTO()
    {
        super();
    }

    public FlaggedDisplayablePortfolioDTO(OwnedPortfolioId ownedPortfolioId)
    {
        super(ownedPortfolioId);
    }

    public FlaggedDisplayablePortfolioDTO(OwnedPortfolioId ownedPortfolioId, UserBaseDTO userBaseDTO, PortfolioDTO portfolioDTO)
    {
        super(ownedPortfolioId, userBaseDTO, portfolioDTO);
    }
    //</editor-fold>
}
