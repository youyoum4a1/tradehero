package com.tradehero.th.api.watchlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WatchlistPositionDTO extends PositionDTO
{
    public static final String WATCHLIST_PRICE_FIELD = "watchlistPrice";

    @JsonProperty(WATCHLIST_PRICE_FIELD)
    @Nullable public Double watchlistPriceRefCcy;
    @Nullable public SecurityCompactDTO securityDTO;

    //<editor-fold desc="Constructors">
    public WatchlistPositionDTO()
    {
        super();
    }
    //</editor-fold>

    @NotNull @Override public String getNiceCurrency()
    {
        if (securityDTO != null
                && securityDTO.currencyDisplay != null
                && !securityDTO.currencyDisplay.isEmpty())
        {
            return securityDTO.currencyDisplay;
        }
        return super.getNiceCurrency();
    }

    @Nullable public Double getInvestedRefCcy()
    {
        if (watchlistPriceRefCcy == null || shares == null)
        {
            return null;
        }
        return watchlistPriceRefCcy * shares;
    }

    @Nullable public Double getInvestedUsd()
    {
        Double investedRefCcy = getInvestedRefCcy();
        if (investedRefCcy == null
                || securityDTO == null
                || securityDTO.toUSDRate == null)
        {
            return null;
        }
        return investedRefCcy * securityDTO.toUSDRate;
    }

    @Nullable public Double getCurrentValueRefCcy()
    {
        if (shares == null
                || securityDTO == null
                || securityDTO.lastPrice == null)
        {
            return null;
        }
        return securityDTO.lastPrice * shares;
    }

    @Nullable public Double getCurrentValueUsd()
    {
        Double currentValueRefCcy = getCurrentValueRefCcy();
        if (currentValueRefCcy == null
                || securityDTO == null
                || securityDTO.toUSDRate == null)
        {
            return null;
        }
        return currentValueRefCcy * securityDTO.toUSDRate;
    }

    @Override public String toString()
    {
        return "WatchlistPositionDTO{" +
                "watchlistPriceRefCcy=" + watchlistPriceRefCcy +
                ", securityDTO=" + securityDTO +
                '}';
    }
}
