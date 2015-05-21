package com.tradehero.th.api.position;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import org.jetbrains.annotations.NotNull;

public class PositionDTOList<PositionDTOType extends PositionDTO>
        extends BaseArrayList<PositionDTOType>
    implements DTO
{


    public void setOnInPeriod(@NotNull LeaderboardMarkUserId leaderboardMarkUserId)
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
