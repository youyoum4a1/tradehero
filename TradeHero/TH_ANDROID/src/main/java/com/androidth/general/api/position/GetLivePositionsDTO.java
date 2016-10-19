package com.androidth.general.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.api.leaderboard.position.LeaderboardMarkUserId;
import com.androidth.general.api.security.SecurityCompactDTOList;
import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class GetLivePositionsDTO implements DTO
{
//    public LivePositionDTOList positions;
//    public SecurityCompactDTOList securities;
//    public int openPositionsCount;
//    public int closedPositionsCount;
//
//    @JsonIgnore
//    public void setOnInPeriod(@NonNull LeaderboardMarkUserId leaderboardMarkUserId)
//    {
//        if (positions != null)
//        {
//            PositionDTOUtil.setOnInPeriodLive(positions, leaderboardMarkUserId);
//        }
//    }
//
//    @Override public String toString()
//    {
//        return "GetLivePositionsDTO{" +
//                "positions=" + positions +
//                ", securities=" + securities +
//                ", openPositionsCount=" + openPositionsCount +
//                ", closedPositionsCount=" + closedPositionsCount +
//                '}';
//    }
    public String response;
}
