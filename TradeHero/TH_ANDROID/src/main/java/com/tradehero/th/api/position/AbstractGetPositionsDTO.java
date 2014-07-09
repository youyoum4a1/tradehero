package com.tradehero.th.api.position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class AbstractGetPositionsDTO<PositionDTOType extends PositionDTO> implements DTO
{
    @Nullable public PositionDTOList<PositionDTOType> positions;
    @Nullable public SecurityCompactDTOList securities;
    public int openPositionsCount;
    public int closedPositionsCount;

    //<editor-fold desc="Constructors">
    public AbstractGetPositionsDTO()
    {
    }

    public AbstractGetPositionsDTO(
            @Nullable PositionDTOList<PositionDTOType> positions,
            @Nullable SecurityCompactDTOList securities,
            int openPositionsCount,
            int closedPositionsCount)
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

    @Nullable
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

    @JsonIgnore
    public void setOnInPeriod(@NotNull LeaderboardMarkUserId leaderboardMarkUserId)
    {
        if (positions != null)
        {
            positions.setOnInPeriod(leaderboardMarkUserId);
        }
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
