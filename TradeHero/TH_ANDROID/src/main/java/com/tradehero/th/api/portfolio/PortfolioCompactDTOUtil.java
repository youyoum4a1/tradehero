package com.tradehero.th.api.portfolio;

import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.SecurityUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 2/26/14.
 */
public class PortfolioCompactDTOUtil
{
    @Inject public PortfolioCompactDTOUtil()
    {
        super();
    }

    // TODO handle refCurrency different from USD
    public Integer getMaxPurchasableShares(PortfolioCompactDTO portfolioCompactDTO, QuoteDTO quoteDTO)
    {
        return getMaxPurchasableShares(portfolioCompactDTO, quoteDTO, true);
    }

    public Integer getMaxPurchasableShares(PortfolioCompactDTO portfolioCompactDTO, QuoteDTO quoteDTO, boolean includeTransactionCost)
    {
        if (quoteDTO == null || portfolioCompactDTO == null)
        {
            return null;
        }
        Double askUsd = quoteDTO.getAskUSD();
        double cashUsd = portfolioCompactDTO.getCashBalanceUsd();
        if (askUsd == null || askUsd == 0)
        {
            return null;
        }
        return (int) Math.floor((cashUsd - (includeTransactionCost ? SecurityUtils.DEFAULT_TRANSACTION_COST : 0)) / askUsd);
    }


}
