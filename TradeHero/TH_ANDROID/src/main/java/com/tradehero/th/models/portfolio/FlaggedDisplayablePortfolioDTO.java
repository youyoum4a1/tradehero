package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseDTO;

/**
 * Created by xavier on 3/5/14.
 */
class FlaggedDisplayablePortfolioDTO extends DisplayablePortfolioDTO
{
    public static final String TAG = FlaggedDisplayablePortfolioDTO.class.getSimpleName();

    public boolean fetchingUser = false;
    public boolean fetchingPortfolio = false;

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
}
