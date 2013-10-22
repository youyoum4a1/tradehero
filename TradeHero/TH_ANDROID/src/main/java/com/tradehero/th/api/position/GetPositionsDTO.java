package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:35 PM To change this template use File | Settings | File Templates. */
public class GetPositionsDTO
{
    public List<PositionDTO> positions;
    public List<SecurityCompactDTO> securities;
    public int openPositionsCount;
    public int closedPositionsCount;

    public GetPositionsDTO()
    {
    }

    public GetPositionsDTO(List<PositionDTO> positions, List<SecurityCompactDTO> securities, int openPositionsCount, int closedPositionsCount)
    {
        this.positions = positions;
        this.securities = securities;
        this.openPositionsCount = openPositionsCount;
        this.closedPositionsCount = closedPositionsCount;
    }

    public List<OwnedPositionId> getFiledPositionIds(PortfolioId portfolioId)
    {
        if (positions == null)
        {
            return null;
        }

        List<OwnedPositionId> ownedPositionIds = new ArrayList<>();

        for (PositionDTO positionDTO: positions)
        {
            ownedPositionIds.add(new OwnedPositionId(positionDTO.userId, portfolioId.key, positionDTO.id, positionDTO.securityId));
        }

        return ownedPositionIds;
    }

    public List<PositionDTO> getOpenPositions()
    {
        return getOpenPositions(true);
    }

    public List<PositionDTO> getClosedPositions()
    {
        return getOpenPositions(false);
    }

    public List<PositionDTO> getPositionsWithUnknownOpenStatus()
    {
        return getOpenPositions(null);
    }

    public List<PositionDTO> getOpenPositions(Boolean open)
    {
        if (positions == null)
        {
            return null;
        }
        List<PositionDTO> openPositions = new ArrayList<>();
        for (PositionDTO positionDTO: positions)
        {
            if (positionDTO.isOpen() == open)
            {
                openPositions.add(positionDTO);
            }
        }
        return openPositions;
    }
}
