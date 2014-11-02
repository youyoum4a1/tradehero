package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
class WatchlistPositionCutDTO implements DTO
{
    // PositionDTOCompact
    public int id;
    public Integer shares;
    public int portfolioId;

    // This price is always in USD
    public Double averagePriceRefCcy;
    @Nullable public String currencyDisplay;
    @Nullable public String currencyISO;

    // PositionDTO
    public int userId;
    public int securityId;
    public Double realizedPLRefCcy;
    public Double unrealizedPLRefCcy;
    public double marketValueRefCcy;
    public Date earliestTradeUtc;
    public Date latestTradeUtc;

    public Double sumInvestedAmountRefCcy;

    public double totalTransactionCostRefCcy;

    // if >1, then the values above relate to a collection of positions, not a single position -- see: MaskOpenPositions()
    public int aggregateCount;

    // WatchlistPositionDTO
    public Double watchlistPrice;
    @Nullable public SecurityId securityIdKey;

    WatchlistPositionCutDTO(@NotNull WatchlistPositionDTO inflated,
            @NotNull SecurityCompactCacheRx securityCompactCache)
    {
        this.id = inflated.id;
        this.shares = inflated.shares;
        this.portfolioId = inflated.portfolioId;
        this.averagePriceRefCcy = inflated.averagePriceRefCcy;
        this.currencyDisplay = inflated.currencyDisplay;
        this.currencyISO = inflated.currencyISO;
        this.userId = inflated.userId;
        this.securityId = inflated.securityId;
        this.realizedPLRefCcy = inflated.realizedPLRefCcy;
        this.unrealizedPLRefCcy = inflated.unrealizedPLRefCcy;
        this.marketValueRefCcy = inflated.marketValueRefCcy;
        this.earliestTradeUtc = inflated.earliestTradeUtc;
        this.latestTradeUtc = inflated.latestTradeUtc;
        this.sumInvestedAmountRefCcy = inflated.sumInvestedAmountRefCcy;
        this.totalTransactionCostRefCcy = inflated.totalTransactionCostRefCcy;
        this.aggregateCount = inflated.aggregateCount;
        this.watchlistPrice = inflated.watchlistPriceRefCcy;
        if (inflated.securityDTO == null)
        {
            this.securityIdKey = null;
        }
        else
        {
            SecurityId key = inflated.securityDTO.getSecurityId();
            securityCompactCache.onNext(key, inflated.securityDTO);
            this.securityIdKey = key;
        }
    }

    @Nullable WatchlistPositionDTO inflate(@NotNull SecurityCompactCacheRx securityCompactCache)
    {
        WatchlistPositionDTO inflated = new WatchlistPositionDTO();

        if (securityIdKey != null)
        {
            SecurityCompactDTO cached = securityCompactCache.getValue(securityIdKey);
            if (cached == null)
            {
                return null;
            }
            inflated.securityDTO = cached;
        }

        inflated.id = id;
        inflated.shares = shares;
        inflated.portfolioId = portfolioId;
        inflated.averagePriceRefCcy = averagePriceRefCcy;
        inflated.currencyDisplay = currencyDisplay;
        inflated.currencyISO = currencyISO;
        inflated.userId = userId;
        inflated.securityId = securityId;
        inflated.realizedPLRefCcy = realizedPLRefCcy;
        inflated.unrealizedPLRefCcy = unrealizedPLRefCcy;
        inflated.marketValueRefCcy = marketValueRefCcy;
        inflated.earliestTradeUtc = earliestTradeUtc;
        inflated.latestTradeUtc = latestTradeUtc;
        inflated.sumInvestedAmountRefCcy = sumInvestedAmountRefCcy;
        inflated.totalTransactionCostRefCcy = totalTransactionCostRefCcy;
        inflated.aggregateCount = aggregateCount;
        inflated.watchlistPriceRefCcy = watchlistPrice;
        return inflated;
    }
}
