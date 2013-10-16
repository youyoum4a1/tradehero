package com.tradehero.th.api.position;

import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:28 PM To change this template use File | Settings | File Templates. */
public class PositionDTO extends PositionDTOCompact
{
    public int userId;
    public int securityId;
    public Double realizedPLRefCcy;
    public Double unrealizedPLRefCcy;
    public double marketValueRefCcy;
    public String earliestTradeUtc;
    public String latestTradeUtc;

    public Double sumInvestedAmountRefCcy;

    public double totalTransactionCostRefCcy;

    // if >1, then the values above relate to a collection of positions, not a single position -- see: MaskOpenPositions()
    public int aggregateCount;

    public OwnedPositionId getOwnedPositionId()
    {
        return new OwnedPositionId(userId, securityId);
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

    public FiledPositionId getFiledPositionId(Integer portfolioId)
    {
        return new FiledPositionId(userId, securityId, portfolioId);
    }

    public static List<FiledPositionId> getFiledPositionIds(Integer portfolioId, List<PositionDTO> positionDTOs)
    {
        if (positionDTOs == null)
        {
            return null;
        }

        List<FiledPositionId> positionIds = new ArrayList<>();

        for (PositionDTO positionDTO: positionDTOs)
        {
            positionIds.add(positionDTO.getFiledPositionId(portfolioId));
        }

        return positionIds;
    }

    public Double getUnrealizedPLRefCcyPercent()
    {
        if (unrealizedPLRefCcy == null)
        {
            return null;
        }

        if (marketValueRefCcy == unrealizedPLRefCcy)
        {
            return null;
        }

        return marketValueRefCcy / (marketValueRefCcy - unrealizedPLRefCcy);
    }
}
