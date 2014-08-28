package com.tradehero.th.api.portfolio;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.quote.QuoteDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;

public class PortfolioCompactDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactDTOUtil()
    {
        super();
    }
    //</editor-fold>

    //<editor-fold desc="Max Purchasable Shares">
    // TODO handle refCurrency different from USD
    @Nullable public Integer getMaxPurchasableShares(PortfolioCompactDTO portfolioCompactDTO, QuoteDTO quoteDTO)
    {
        return getMaxPurchasableShares(portfolioCompactDTO, quoteDTO, true);
    }

    @Nullable public Integer getMaxPurchasableShares(
            PortfolioCompactDTO portfolioCompactDTO,
            QuoteDTO quoteDTO,
            boolean includeTransactionCostUsd)
    {
        if (quoteDTO == null || portfolioCompactDTO == null)
        {
            return null;
        }
        double txnCostUsd = portfolioCompactDTO.getProperTxnCostUsd();
        Double askUsd = quoteDTO.getAskUSD();
        double cashUsd = portfolioCompactDTO.getCashBalanceUsd();
        if (askUsd == null || askUsd == 0)
        {
            return null;
        }
        return (int) Math.floor((cashUsd - (includeTransactionCostUsd ? txnCostUsd : 0)) / askUsd);
    }
    //</editor-fold>

    public String getPortfolioSubtitle(Context context, PortfolioCompactDTO portfolioCompactDTO, String userName)
    {
        if (portfolioCompactDTO != null)
        {
            if (portfolioCompactDTO.openPositionsCount > 0)
            {
                if (userName != null && !userName.isEmpty())
                {
                    return context.getString(
                            R.string.portfolio_description_count_open_positions_other,
                            portfolioCompactDTO.openPositionsCount,
                            userName);
                }
                return context.getString(
                        R.string.portfolio_description_count_open_positions_you,
                        portfolioCompactDTO.openPositionsCount);
            }
            if (portfolioCompactDTO.closedPositionsCount > 0)
            {
                if (userName != null && !userName.isEmpty())
                {
                    return context.getString(
                            R.string.portfolio_description_count_closed_positions_other,
                            portfolioCompactDTO.closedPositionsCount,
                            userName);
                }
                return context.getString(
                        R.string.portfolio_description_count_closed_positions_you,
                        portfolioCompactDTO.closedPositionsCount);
            }
            if (portfolioCompactDTO.watchlistPositionsCount > 0)
            {
                if (userName != null && !userName.isEmpty())
                {
                    return context.getString(
                            R.string.portfolio_description_count_watch_positions_other,
                            portfolioCompactDTO.watchlistPositionsCount,
                            userName);
                }
                return context.getString(
                        R.string.portfolio_description_count_watch_positions_you,
                        portfolioCompactDTO.watchlistPositionsCount);
            }
        }
        return null;
    }
}
