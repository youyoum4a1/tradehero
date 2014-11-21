package com.tradehero.th.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PositionDTO extends PositionDTOCompact
{
    public int userId;
    public int securityId;
    // This value is always in the portfolio currency
    public Double realizedPLRefCcy;
    // This value is always in the portfolio currency
    public Double unrealizedPLRefCcy;
    // This value is always in the portfolio currency
    public double marketValueRefCcy;
    public Date earliestTradeUtc;
    public Date latestTradeUtc;

    // This value is always in the portfolio currency
    public Double sumInvestedAmountRefCcy;

    // This value is always in the portfolio currency
    public double totalTransactionCostRefCcy;

    // if >1, then the values above relate to a collection of positions, not a single position -- see: MaskOpenPositions()
    public int aggregateCount;

    //<editor-fold desc="Constructors">
    public PositionDTO()
    {
        super();
    }
    //</editor-fold>

    public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }

    @JsonIgnore
    public OwnedPortfolioId getOwnedPortfolioId()
    {
        return new OwnedPortfolioId(userId, portfolioId);
    }

    @JsonIgnore
    public OwnedPositionId getOwnedPositionId()
    {
        return new OwnedPositionId(userId, portfolioId, id);
    }

    @JsonIgnore
    public PositionDTOKey getPositionDTOKey()
    {
        return getOwnedPositionId();
    }

    public static List<PositionDTOKey> getPositionDTOKeys(List<PositionDTO> positionDTOs)
    {
        if (positionDTOs == null)
        {
            return null;
        }

        List<PositionDTOKey> positionIds = new ArrayList<>();

        for (PositionDTO positionDTO: positionDTOs)
        {
            positionIds.add(positionDTO.getPositionDTOKey());
        }

        return positionIds;
    }

    @NonNull public SecurityIntegerId getSecurityIntegerId()
    {
        return new SecurityIntegerId(securityId);
    }

    @Nullable
    public static List<PositionDTOKey> getFiledPositionIds(@Nullable List<PositionDTO> positionDTOs)
    {
        if (positionDTOs == null)
        {
            return null;
        }

        List<PositionDTOKey> positionDTOKeys = new ArrayList<>();

        for (PositionDTO positionDTO: positionDTOs)
        {
            positionDTOKeys.add(positionDTO.getPositionDTOKey());
        }

        return positionDTOKeys;
    }

    public Double getROISinceInception()
    {
        if (shares == null || realizedPLRefCcy == null || unrealizedPLRefCcy == null)
        {
            return null;
        }

        double numberToDisplay = realizedPLRefCcy;
        if (isOpen())
        {
            numberToDisplay += unrealizedPLRefCcy;
        }

        // divide by cost basis, if possible
        if (sumInvestedAmountRefCcy == null || sumInvestedAmountRefCcy == 0)
        {
            return null;
        }
        else
        {
            numberToDisplay /= sumInvestedAmountRefCcy ;
        }
        return numberToDisplay;
    }

    public boolean isLocked()
    {
        return this.securityId < 0;
    }

    @Override public String toString()
    {
        return "PositionDTO{" +
                "id=" + id +
                ", shares=" + shares +
                ", portfolioId=" + portfolioId +
                ", averagePriceRefCcy=" + averagePriceRefCcy +
                ", currencyDisplay=" + currencyDisplay +
                ", currencyISO=" + currencyISO +
                ", userId=" + userId +
                ", securityId=" + securityId +
                ", realizedPLRefCcy=" + realizedPLRefCcy +
                ", unrealizedPLRefCcy=" + unrealizedPLRefCcy +
                ", marketValueRefCcy=" + marketValueRefCcy +
                ", earliestTradeUtc=" + earliestTradeUtc +
                ", latestTradeUtc=" + latestTradeUtc +
                ", sumInvestedAmountRefCcy=" + sumInvestedAmountRefCcy +
                ", totalTransactionCostRefCcy=" + totalTransactionCostRefCcy +
                ", aggregateCount=" + aggregateCount +
                '}';
    }
}
