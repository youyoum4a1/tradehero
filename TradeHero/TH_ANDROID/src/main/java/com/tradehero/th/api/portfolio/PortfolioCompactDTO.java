package com.tradehero.th.api.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.SecurityUtils;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PortfolioCompactDTO implements DTO
{
    public static final String DEFAULT_TITLE = "Default";

    public int id;
    //<editor-fold desc="Populated on client side">
    public int userId;
    //</editor-fold>

    public Integer providerId;
    public String title;

    public double cashBalance;
    public double totalValue;
    public double totalExtraCashPurchased;
    public double totalExtraCashGiven;

    public Double roiSinceInception;
    public boolean isWatchlist;
    public int openPositionsCount;
    public int closedPositionsCount;
    public int watchlistPositionsCount;
    public Date markingAsOfUtc;
    public String currencyDisplay;
    public String currencyISO;
    @Nullable public Double refCcyToUsdRate;

    //<editor-fold desc="Constructors">
    public PortfolioCompactDTO()
    {
    }
    //</editor-fold>

    @JsonIgnore @NotNull public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }

    @JsonIgnore @NotNull public PortfolioId getPortfolioId()
    {
        return new PortfolioId(id);
    }

    @JsonIgnore @NotNull public OwnedPortfolioId getOwnedPortfolioId()
    {
        return new OwnedPortfolioId(userId, id);
    }

    // Do NOT rename to getProviderId or providerId will always be null
    @JsonIgnore @Nullable public ProviderId getProviderIdKey()
    {
        if (providerId == null)
        {
            return null;
        }
        return new ProviderId(providerId);
    }

    @JsonIgnore public boolean isDefault()
    {
        return providerId == null && !isWatchlist;
    }

    @JsonIgnore public double getTotalExtraCash()
    {
        return totalExtraCashGiven + totalExtraCashPurchased;
    }

    @Override public int hashCode()
    {
        return Integer.valueOf(id).hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof PortfolioCompactDTO) && equals((PortfolioCompactDTO) other);
    }

    public boolean equals(PortfolioCompactDTO other)
    {
        if (other == null)
        {
            return false;
        }
        return Integer.valueOf(id).equals(other.id);
    }

    @JsonIgnore public double getCashBalanceUsd()
    {
        return cashBalance * getProperRefCcyToUsdRate();
    }

    @JsonIgnore @NotNull public String getNiceCurrency()
    {
        if (currencyDisplay != null && !currencyDisplay.isEmpty())
        {
            return currencyDisplay;
        }
        return SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
    }

    @JsonIgnore public double getProperRefCcyToUsdRate()
    {
        return refCcyToUsdRate == null ? 1 : refCcyToUsdRate;
    }

    @Override @NotNull public String toString()
    {
        return "[PortfolioCompactDTO " +
                "cashBalance=" + cashBalance +
                ", id=" + id +
                ", providerId=" + providerId +
                ", title='" + title + '\'' +
                ", totalValue=" + totalValue +
                ", totalExtraCashPurchased=" + totalExtraCashPurchased +
                ", totalExtraCashGiven=" + totalExtraCashGiven +
                ", roiSinceInception" + roiSinceInception +
                ", isWatchlist=" + isWatchlist +
                ", openPositionsCount=" + openPositionsCount +
                ", closedPositionsCount=" + closedPositionsCount +
                ", watchlistPositionsCount=" + watchlistPositionsCount +
                ", markingAsOfUtc=" + markingAsOfUtc +
                ", currencyDisplay='" + currencyDisplay + '\'' +
                ", refCcyToUsdRate=" + refCcyToUsdRate +
                ", userId=" + userId +
                ']';
    }
}