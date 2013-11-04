package com.tradehero.th.api.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/4/13 Time: 4:46 PM To change this template use File | Settings | File Templates. */
abstract public class AbstractGetPositionsDTO<PositionType extends PositionDTO> implements DTO
{
    public static final String TAG = AbstractGetPositionsDTO.class.getSimpleName();

    public List<PositionType> positions;
    public List<SecurityCompactDTO> securities;
    public int openPositionsCount;
    public int closedPositionsCount;

    public AbstractGetPositionsDTO()
    {
    }

    public AbstractGetPositionsDTO(List<PositionType> positions, List<SecurityCompactDTO> securities, int openPositionsCount, int closedPositionsCount)
    {
        this.positions = positions;
        this.securities = securities;
        this.openPositionsCount = openPositionsCount;
        this.closedPositionsCount = closedPositionsCount;
    }

    public List<PositionType> getOpenPositions()
    {
        return getOpenPositions(true);
    }

    public List<PositionType> getClosedPositions()
    {
        return getOpenPositions(false);
    }

    public List<PositionType> getPositionsWithUnknownOpenStatus()
    {
        return getOpenPositions(null);
    }

    public List<PositionType> getOpenPositions(Boolean open)
    {
        if (positions == null)
        {
            return null;
        }
        List<PositionType> openPositions = new ArrayList<>();
        for (PositionType positionDTO: positions)
        {
            if (positionDTO.isOpen() == open)
            {
                openPositions.add(positionDTO);
            }
        }
        return openPositions;
    }
}
