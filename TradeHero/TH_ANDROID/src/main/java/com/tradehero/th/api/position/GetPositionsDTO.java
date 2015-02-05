package com.tradehero.th.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.security.SecurityCompactDTOList;

public class GetPositionsDTO implements DTO
{
    @Nullable public PositionDTOList<PositionDTO> positions;
    @Nullable public SecurityCompactDTOList securities;
    public int openPositionsCount;
    public int closedPositionsCount;

    @JsonIgnore
    public void setOnInPeriod(@NonNull LeaderboardMarkUserId leaderboardMarkUserId)
    {
        if (positions != null)
        {
            positions.setOnInPeriod(leaderboardMarkUserId);
        }
    }

    @Override public String toString()
    {
        return "GetPositionsDTO{" +
                "positions=" + positions +
                ", securities=" + securities +
                ", openPositionsCount=" + openPositionsCount +
                ", closedPositionsCount=" + closedPositionsCount +
                '}';
    }
}
