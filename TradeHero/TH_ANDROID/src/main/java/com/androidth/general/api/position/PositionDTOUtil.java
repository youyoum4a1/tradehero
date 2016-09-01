package com.androidth.general.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.leaderboard.position.LeaderboardMarkUserId;
import java.util.Date;
import java.util.List;

public class PositionDTOUtil extends PositionDTOCompactUtil
{
    @Nullable public static Date getEarliestTradeUtc(@NonNull List<? extends PositionDTO> positionDTOs)
    {
        Date earliest = null;

        for (PositionDTO positionDTO : positionDTOs)
        {
            if (positionDTO != null && positionDTO.getEarliestTradeUtc() != null)
            {
                if (earliest == null || positionDTO.getEarliestTradeUtc().before(earliest))
                {
                    earliest = positionDTO.getEarliestTradeUtc();
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
                if (latest == null || positionDTO.getLatestTradeUtc().after(latest))
                {
                    latest = positionDTO.getLatestTradeUtc();
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
