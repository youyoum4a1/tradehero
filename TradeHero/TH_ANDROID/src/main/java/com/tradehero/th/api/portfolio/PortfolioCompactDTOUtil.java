package com.tradehero.th.api.portfolio;

import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.internal.util.Predicate;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.models.resource.ResourceUtil;
import com.tradehero.th.utils.SecurityUtils;
import timber.log.Timber;

public class PortfolioCompactDTOUtil
{
    //<editor-fold desc="Max Purchasable Shares">
    // TODO handle refCurrency different from USD
    @Nullable public static Integer getMaxPurchasableShares(
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

    @Nullable public static Integer getMaxPurchasableShares(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition)
    {
        if (portfolioCompactDTO == null)
        {
            return null;
        }

        if (closeablePosition != null && portfolioCompactDTO.id != closeablePosition.portfolioId)
        {
            throw new IllegalArgumentException("Portfolio ids do not match " + portfolioCompactDTO.id + " and " + closeablePosition.portfolioId);
        }

        if (closeablePosition != null
                && closeablePosition.positionStatus != null
                && closeablePosition.positionStatus.equals(PositionStatus.SHORT))
        {
            // TODO return null if transaction cost cannot be covered
            return closeablePosition.shares == null ? null : Math.abs(closeablePosition.shares);
        }
        return getMaxPurchasableShares(portfolioCompactDTO, quoteDTO);
    }
    //</editor-fold>

    @Nullable public static Integer getMaxSellableShares(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition)
    {
        if (portfolioCompactDTO == null)
        {
            return null;
        }

        if (closeablePosition != null
                && closeablePosition.positionStatus != null
                && closeablePosition.positionStatus.equals(PositionStatus.LONG))
        {
            // TODO return null if transaction cost cannot be covered
            return closeablePosition.shares == null ? null : Math.abs(closeablePosition.shares);
        }

        if (quoteDTO == null)
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

    @Nullable public static String getPortfolioSubtitle(
            @NonNull Resources resources,
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable String userName)
    {
        if (portfolioCompactDTO != null)
        {
            if (portfolioCompactDTO.openPositionsCount > 0)
            {
                if (userName != null && !userName.isEmpty())
                {
                    return resources.getString(
                            R.string.portfolio_description_count_open_positions_other,
                            portfolioCompactDTO.openPositionsCount,
                            userName);
                }
                return resources.getString(
                        R.string.portfolio_description_count_open_positions_you,
                        portfolioCompactDTO.openPositionsCount);
            }
            if (portfolioCompactDTO.closedPositionsCount > 0)
            {
                if (userName != null && !userName.isEmpty())
                {
                    return resources.getString(
                            R.string.portfolio_description_count_closed_positions_other,
                            portfolioCompactDTO.closedPositionsCount,
                            userName);
                }
                return resources.getString(
                        R.string.portfolio_description_count_closed_positions_you,
                        portfolioCompactDTO.closedPositionsCount);
            }
            if (portfolioCompactDTO.watchlistPositionsCount > 0)
            {
                if (userName != null && !userName.isEmpty())
                {
                    return resources.getString(
                            R.string.portfolio_description_count_watch_positions_other,
                            portfolioCompactDTO.watchlistPositionsCount,
                            userName);
                }
                return resources.getString(
                        R.string.portfolio_description_count_watch_positions_you,
                        portfolioCompactDTO.watchlistPositionsCount);
            }
        }
        return null;
    }

    @NonNull public static MarginCloseOutState getMarginCloseOutState(
            @NonNull Resources resources,
            double marginCloseOut)
    {
        for (MarginCloseOutState marginState : MarginCloseOutState.values())
        {
            if (ResourceUtil.getFloat(resources, marginState.lowerBoundResId) <= marginCloseOut
                    && marginCloseOut <= ResourceUtil.getFloat(resources, marginState.upperBoundResId))
            {
                return marginState;
            }
        }
        Timber.e(new IllegalArgumentException(), "Failed to get MarginCloseOutState for %f", marginCloseOut);
        return MarginCloseOutState.DANGER;
    }

    @Nullable public static QuoteDTO createQuoteInPortfolioRefCcy(@Nullable QuoteDTO quoteDTO, @Nullable PortfolioCompactDTO portfolioCompactDTO)
    {
        if (quoteDTO == null
                || quoteDTO.toUSDRate == null
                || portfolioCompactDTO == null
                || portfolioCompactDTO.refCcyToUsdRate == null
                || portfolioCompactDTO.refCcyToUsdRate == 0)
        {
            return null;
        }

        QuoteDTO converted;
        try
        {
            converted = quoteDTO.clone();
        } catch (CloneNotSupportedException e)
        {
            Timber.e(e, "Could not clone");
            converted = new QuoteDTO();
        }
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

    @DrawableRes public static int getIconResId(@NonNull PortfolioCompactDTO portfolioDTO)
    {
        int imageResId = R.drawable.ic_portfolio_stocks;
        if (portfolioDTO.providerId != null)
        {
            imageResId = R.drawable.ic_portfolio_competition;
        }
        else if (portfolioDTO.isDefault())
        {
            if (portfolioDTO.isFx())
            {
                imageResId = R.drawable.ic_portfolio_fx;
            }
            else
            {
                imageResId = R.drawable.ic_portfolio_stocks;
            }
        }
        else if (portfolioDTO.isWatchlist)
        {
            imageResId = R.drawable.ic_portfolio_favorites;
        }
        return imageResId;
    }

    @ColorRes @Nullable public static Integer getIconTintResId(@NonNull PortfolioCompactDTO portfolioDTO)
    {
        Integer colorResId = null;
        if (portfolioDTO.isWatchlist)
        {
            colorResId = R.color.watchlist_button_color;
        }
        return colorResId;
    }

    @NonNull public static PortfolioCompactDTO getPurchaseApplicablePortfolio(
            @NonNull PortfolioCompactDTOList portfolioCompactDTOs,
            @Nullable final OwnedPortfolioId potential,
            @Nullable final ProviderId providerId,
            @Nullable SecurityId securityId)
    {
        if (potential != null)
        {
            PortfolioCompactDTO candidate = portfolioCompactDTOs.findFirstWhere(new Predicate<PortfolioCompactDTO>()
            {
                @Override public boolean apply(PortfolioCompactDTO portfolioCompactDTO)
                {
                    return portfolioCompactDTO.getOwnedPortfolioId().equals(potential);
                }
            });
            if (candidate != null)
            {
                return candidate;
            }
        }
        if (providerId != null)
        {
            PortfolioCompactDTO candidate= portfolioCompactDTOs.findFirstWhere(new Predicate<PortfolioCompactDTO>()
            {
                @Override public boolean apply(PortfolioCompactDTO portfolioCompactDTO)
                {
                    return portfolioCompactDTO.providerId != null && portfolioCompactDTO.providerId.equals(providerId.key);
                }
            });
            if (candidate != null)
            {
                return candidate;
            }
        }
        if (securityId != null)
        {
            PortfolioCompactDTO candidate = SecurityUtils.isFX(securityId)
                    ? portfolioCompactDTOs.getDefaultFxPortfolio()
                    : portfolioCompactDTOs.getDefaultPortfolio();
            if (candidate != null)
            {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Unhandled case " + potential + ", " + providerId + ", " + securityId);
    }}
