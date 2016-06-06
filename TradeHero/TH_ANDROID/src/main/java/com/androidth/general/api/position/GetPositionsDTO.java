package com.androidth.general.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.leaderboard.position.LeaderboardMarkUserId;
import com.androidth.general.api.security.SecurityCompactDTOList;

public class GetPositionsDTO implements DTO
{
    @Nullable public PositionDTOList positions;
    @Nullable public SecurityCompactDTOList securities;
    public int openPositionsCount;
    public int closedPositionsCount;

    @JsonIgnore
    public void setOnInPeriod(@NonNull LeaderboardMarkUserId leaderboardMarkUserId)
    {
        if (positions != null)
        {
            PositionDTOUtil.setOnInPeriod(positions, leaderboardMarkUserId);
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
