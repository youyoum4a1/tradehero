package com.tradehero.th.api.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityIntegerId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:28 PM To change this template use File | Settings | File Templates. */
public class PositionDTO extends PositionDTOCompact
{
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

    public OwnedPositionId getOwnedPositionId()
    {
        return new OwnedPositionId(userId, portfolioId, id);
    }

    public static List<OwnedPositionId> getOwnedPositionIds(List<PositionDTO> positionDTOs)
    {
        if (positionDTOs == null)
        {
            return null;
        }

        List<OwnedPositionId> positionIds = new ArrayList<>();

        for (PositionDTO positionDTO: positionDTOs)
        {
            positionIds.add(positionDTO.getOwnedPositionId());
        }

        return positionIds;
    }

    public OwnedPositionId getOwnedPositionId(Integer portfolioId)
    {
        return new OwnedPositionId(userId, portfolioId, id);
    }

    public SecurityIntegerId getSecurityIntegerId()
    {
        return new SecurityIntegerId(securityId);
    }

    public static List<OwnedPositionId> getFiledPositionIds(Integer portfolioId, List<PositionDTO> positionDTOs)
    {
        if (positionDTOs == null)
        {
            return null;
        }

        List<OwnedPositionId> ownedPositionIds = new ArrayList<>();

        for (PositionDTO positionDTO: positionDTOs)
        {
            ownedPositionIds.add(positionDTO.getOwnedPositionId(portfolioId));
        }

        return ownedPositionIds;
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

}
