package com.tradehero.th.api.portfolio;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.models.resource.ResourceUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class PortfolioCompactDTOUtil
{
    @NonNull protected final ResourceUtil resourceUtil;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactDTOUtil(@NonNull ResourceUtil resourceUtil)
    {
        super();
        this.resourceUtil = resourceUtil;
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
        double availableUsd;
        if (portfolioCompactDTO.marginAvailableRefCcy != null
                && portfolioCompactDTO.leverage != null)
        {
            availableUsd = portfolioCompactDTO.marginAvailableRefCcy
                    * portfolioCompactDTO.leverage
                    * portfolioCompactDTO.getProperRefCcyToUsdRate();
        }
        else
        {
            availableUsd = portfolioCompactDTO.getCashBalanceUsd();
        }
        if (askUsd == null || askUsd == 0)
        {
            return null;
        }
        return (int) Math.floor((availableUsd - (includeTransactionCostUsd ? txnCostUsd : 0)) / askUsd);
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

    @NonNull public MarginCloseOutState getMarginCloseOutState(
            @NonNull Resources resources,
            double marginCloseOut)
    {
        for (MarginCloseOutState marginState : MarginCloseOutState.values())
        {
            if (resourceUtil.getFloat(resources, marginState.lowerBoundResId) <= marginCloseOut
                    && marginCloseOut <= resourceUtil.getFloat(resources, marginState.upperBoundResId))
            {
                return marginState;
            }
        }
        Timber.e(new IllegalArgumentException(), "Failed to get MarginCloseOutState for %f", marginCloseOut);
        throw new IllegalArgumentException();
    }
}
