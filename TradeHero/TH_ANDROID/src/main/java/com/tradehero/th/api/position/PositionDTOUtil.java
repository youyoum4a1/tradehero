package com.ayondo.academy.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.leaderboard.position.LeaderboardMarkUserId;
import java.util.Date;
import java.util.List;

public class PositionDTOUtil extends PositionDTOCompactUtil
{
    @Nullable public static Date getEarliestTradeUtc(@NonNull List<? extends PositionDTO> positionDTOs)
    {
        Date earliest = null;

        for (PositionDTO positionDTO : positionDTOs)
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

    @Nullable public static Date getLatestTradeUtc(@NonNull List<? extends PositionDTO> positionDTOs)
    {
        Date latest = null;

        for (PositionDTO positionDTO : positionDTOs)
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

    public static void setOnInPeriod(@NonNull List<? extends PositionDTO> positionDTOs,
            @NonNull LeaderboardMarkUserId leaderboardMarkUserId)
    {
        for (PositionDTO positionDTO : positionDTOs)
        {
            if (positionDTO instanceof PositionInPeriodDTO)
            {
                ((PositionInPeriodDTO) positionDTO).setLeaderboardMarkUserId(leaderboardMarkUserId);
            }
        }
    }


}
