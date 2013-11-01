package com.tradehero.th.api.leaderboard.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julien on 1/11/13
 */

public class GetLeaderboardPositionDTO implements DTO
{
    public List<PositionInPeriodDTO> positions;
    public List<SecurityCompactDTO> securities;
    public int openPositionsCount;
    public int closedPositionsCount;

    public GetLeaderboardPositionDTO()
    {
    }

    public GetLeaderboardPositionDTO(List<PositionInPeriodDTO> positions, List<SecurityCompactDTO> securities, int openPositionsCount, int closedPositionsCount)
    {
        this.positions = positions;
        this.securities = securities;
        this.openPositionsCount = openPositionsCount;
        this.closedPositionsCount = closedPositionsCount;
    }

    public List<OwnedLbPositionId> getFiledPositionIds(LbUserId lbmuId)
    {
        if (positions == null)
        {
            return null;
        }

        List<OwnedLbPositionId> lbmupIds = new ArrayList<>();

        for (PositionInPeriodDTO positionDTO: positions)
        {
            lbmupIds.add(new OwnedLbPositionId(lbmuId, positionDTO.id));
        }

        return lbmupIds;
    }

    public List<PositionInPeriodDTO> getOpenPositions()
    {
        return getOpenPositions(true);
    }

    public List<PositionInPeriodDTO> getClosedPositions()
    {
        return getOpenPositions(false);
    }

    public List<PositionInPeriodDTO> getPositionsWithUnknownOpenStatus()
    {
        return getOpenPositions(null);
    }

    public List<PositionInPeriodDTO> getOpenPositions(Boolean open)
    {
        if (positions == null)
        {
            return null;
        }
        List<PositionInPeriodDTO> openPositions = new ArrayList<>();
        for (PositionInPeriodDTO positionDTO: positions)
        {
            if (positionDTO.isOpen() == open)
            {
                openPositions.add(positionDTO);
            }
        }
        return openPositions;
    }
}

