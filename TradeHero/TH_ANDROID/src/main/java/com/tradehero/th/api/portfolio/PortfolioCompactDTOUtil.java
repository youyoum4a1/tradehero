package com.tradehero.th.api.portfolio;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionStatus;
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
    @Nullable public Integer getMaxPurchasableShares(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO)
    {
        if (quoteDTO == null || portfolioCompactDTO == null)
        {
            return null;
        }
        Double quotePriceUsd = quoteDTO.getAskUSD();
        if (quotePriceUsd == null || quotePriceUsd == 0)
        {
            return null;
        }
        double availableUsd = portfolioCompactDTO.getUsableForTransactionUsd();
        double txnCostUsd = portfolioCompactDTO.getProperTxnCostUsd();
        return (int) Math.floor((availableUsd - txnCostUsd) / quotePriceUsd);
    }

    @Nullable public Integer getMaxPurchasableShares(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact positionDTOCompact)
    {
        if (portfolioCompactDTO == null)
        {
            return null;
        }

        if (positionDTOCompact != null && portfolioCompactDTO.id != positionDTOCompact.portfolioId)
        {
            throw new IllegalArgumentException("Portfolio ids do not match " + portfolioCompactDTO.id + " and " + positionDTOCompact.portfolioId);
        }

        if (positionDTOCompact != null
                && positionDTOCompact.positionStatus != null
                && positionDTOCompact.positionStatus.equals(PositionStatus.SHORT))
        {
            // TODO return null if transaction cost cannot be covered
            return positionDTOCompact.shares == null ? null : Math.abs(positionDTOCompact.shares);
        }
        return getMaxPurchasableShares(portfolioCompactDTO, quoteDTO);
    }
    //</editor-fold>

    @Nullable public Integer getMaxSellableShares(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO)
    {
        if (quoteDTO == null || portfolioCompactDTO == null)
        {
            return null;
        }
        Double quotePriceUsd = quoteDTO.getBidUSD();
        if (quotePriceUsd == null || quotePriceUsd == 0)
        {
            return null;
        }
        if (!portfolioCompactDTO.usesMargin())
        {
            return 0;
        }
        double availableUsd = portfolioCompactDTO.getUsableForTransactionUsd();
        double txnCostUsd = portfolioCompactDTO.getProperTxnCostUsd();
        return (int) Math.floor((availableUsd - txnCostUsd) / quotePriceUsd);
    }

    @Nullable public Integer getMaxSellableShares(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact positionDTOCompact)
    {
        if (portfolioCompactDTO == null)
        {
            return null;
        }

        if (positionDTOCompact != null && portfolioCompactDTO.id != positionDTOCompact.portfolioId)
        {
            throw new IllegalArgumentException("Portfolio ids do not match " + portfolioCompactDTO.id + " and " + positionDTOCompact.portfolioId);
        }

        if (positionDTOCompact != null
                && positionDTOCompact.positionStatus != null
                && positionDTOCompact.positionStatus.equals(PositionStatus.LONG))
        {
            // TODO return null if transaction cost cannot be covered
            return positionDTOCompact.shares == null ? null : Math.abs(positionDTOCompact.shares);
        }
        return getMaxSellableShares(portfolioCompactDTO, quoteDTO);
    }

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

    @Nullable public QuoteDTO createQuoteInPortfolioRefCcy(@Nullable QuoteDTO quoteDTO, @Nullable PortfolioCompactDTO portfolioCompactDTO)
    {
        if (quoteDTO == null
                || quoteDTO.toUSDRate == null
                || portfolioCompactDTO == null
                || portfolioCompactDTO.refCcyToUsdRate == null
                || portfolioCompactDTO.refCcyToUsdRate == 0)
        {
            return null;
        }

        QuoteDTO converted = quoteDTO.clone();
        if (quoteDTO.bid != null)
        {
            converted.bid = quoteDTO.bid * quoteDTO.toUSDRate / portfolioCompactDTO.refCcyToUsdRate;
        }
        if (quoteDTO.ask != null)
        {
            converted.ask = quoteDTO.ask * quoteDTO.toUSDRate / portfolioCompactDTO.refCcyToUsdRate;
        }
        converted.toUSDRate = portfolioCompactDTO.refCcyToUsdRate;
        converted.toUSDRateDate = null;
        converted.currencyISO = portfolioCompactDTO.currencyISO;
        converted.currencyDisplay = portfolioCompactDTO.currencyDisplay;
        return converted;
    }
}
