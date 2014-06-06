package com.tradehero.th.api.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class PositionDTOList<PositionDTOType extends PositionDTO>
        extends ArrayList<PositionDTOType>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public PositionDTOList(int capacity)
    {
        super(capacity);
    }

    public PositionDTOList()
    {
        super();
    }

    public PositionDTOList(Collection<? extends PositionDTOType> collection)
    {
        super(collection);
    }
    //</editor-fold>

    public Date getEarliestTradeUtc()
    {
        Date earliest = null;

        for (PositionDTOType positionDTO : this)
        {
            if (positionDTO != null && positionDTO.earliestTradeUtc != null)
            {
                if (earliest == null || positionDTO.earliestTradeUtc.before(earliest))
                {
                    earliest = positionDTO.earliestTradeUtc;
                }
            }
        }

        return earliest;
    }

    public Date getLatestTradeUtc()
    {
        Date latest = null;

        for (PositionDTOType positionDTO : this)
        {
            if (positionDTO != null && positionDTO.latestTradeUtc != null)
            {
                if (latest == null || positionDTO.latestTradeUtc.after(latest))
                {
                    latest = positionDTO.latestTradeUtc;
                }
            }
        }

        return latest;
    }

    public void setOnInPeriod(LeaderboardMarkUserId leaderboardMarkUserId)
    {
        for (PositionDTOType positionDTO : this)
        {
            if (positionDTO instanceof PositionInPeriodDTO)
            {
                ((PositionInPeriodDTO) positionDTO).setLeaderboardMarkUserId(leaderboardMarkUserId);
            }
        }
    }
}
