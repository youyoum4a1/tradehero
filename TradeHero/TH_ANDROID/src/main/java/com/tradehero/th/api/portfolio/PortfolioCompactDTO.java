package com.tradehero.th.api.portfolio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.SecurityUtils;
import java.util.Date;

public class PortfolioCompactDTO implements DTO
{
    public static final String DEFAULT_TITLE = "Default";

    public int id;
    //<editor-fold desc="Populated on client side">
    @NonNull public Integer userId;
    //</editor-fold>

    public Integer providerId;
    public String title;

    @Nullable public AssetClass portfolioType;

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
    @Nullable public Double txnCostUsd;

    public Double leverage;
    public Double nav; // Net asset value
    public Double marginAvailableRefCcy;
    public Double marginUsedRefCcy;
    public Double unrealizedPLRefCcy;
    @Nullable public Double marginCloseOutPercent;

    //<editor-fold desc="Constructors">
    public PortfolioCompactDTO()
    {
    }
    //</editor-fold>

    @JsonIgnore @NonNull public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }

    @JsonIgnore @NonNull public PortfolioId getPortfolioId()
    {
        return new PortfolioId(id);
    }

    @JsonIgnore @NonNull public OwnedPortfolioId getOwnedPortfolioId()
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

    @JsonIgnore public boolean isFx()
    {
        return portfolioType != null && portfolioType.equals(AssetClass.FX);
    }

    @JsonIgnore public boolean isAllowedAddCash()
    {
        return isDefault();
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

    @JsonIgnore @NonNull public String getNiceCurrency()
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

    @JsonIgnore public double getProperTxnCostUsd()
    {
        return txnCostUsd != null ? txnCostUsd : SecurityUtils.DEFAULT_TRANSACTION_COST_USD;
    }

    @Override @NonNull public String toString()
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
                ", txnCostUsd=" + txnCostUsd +
                ", userId=" + userId +
                ", marginAvailableRefCcy=" + marginAvailableRefCcy +
                ", marginCloseOutPercent=" + marginCloseOutPercent +
                ", leverage=" + leverage +
                ']';
    }
}