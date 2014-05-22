package com.tradehero.th.api.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.ArrayList;
import java.util.List;

abstract public class AbstractGetPositionsDTO<PositionDTOType extends PositionDTO> implements DTO
{
    public PositionDTOList<PositionDTOType> positions;
    public List<SecurityCompactDTO> securities;
    public int openPositionsCount;
    public int closedPositionsCount;

    //<editor-fold desc="Constructors">
    public AbstractGetPositionsDTO()
    {
    }

    public AbstractGetPositionsDTO(PositionDTOList<PositionDTOType> positions, List<SecurityCompactDTO> securities, int openPositionsCount, int closedPositionsCount)
    {
        this.positions = positions;
        this.securities = securities;
        this.openPositionsCount = openPositionsCount;
        this.closedPositionsCount = closedPositionsCount;
    }
    //</editor-fold>

    public List<PositionDTOType> getOpenPositions()
    {
        return getOpenPositions(true);
    }

    public List<PositionDTOType> getClosedPositions()
    {
        return getOpenPositions(false);
    }

    public List<PositionDTOType> getPositionsWithUnknownOpenStatus()
    {
        return getOpenPositions(null);
    }

    public List<PositionDTOType> getOpenPositions(Boolean open)
    {
        if (positions == null)
        {
            return null;
        }
        List<PositionDTOType> openPositions = new ArrayList<>();
        for (PositionDTOType positionDTO: positions)
        {
            if (positionDTO.isOpen() == open)
            {
                openPositions.add(positionDTO);
            }
        }
        return openPositions;
    }

    @Override public String toString()
    {
        return "AbstractGetPositionsDTO{" +
                "positions=" + positions +
                ", securities=" + securities +
                ", openPositionsCount=" + openPositionsCount +
                ", closedPositionsCount=" + closedPositionsCount +
                '}';
    }
}
