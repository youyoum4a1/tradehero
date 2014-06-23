package com.tradehero.th.models.position;

import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DTOProcessorGetPositions implements DTOProcessor<GetPositionsDTO>
{
    @NotNull private final LeaderboardMarkUserId leaderboardMarkUserId;

    public DTOProcessorGetPositions(@NotNull LeaderboardMarkUserId leaderboardMarkUserId)
    {
        this.leaderboardMarkUserId = leaderboardMarkUserId;
    }

    @Override @Nullable public GetPositionsDTO process(@Nullable GetPositionsDTO value)
    {
        if (value != null)
        {
            value.setOnInPeriod(leaderboardMarkUserId);
        }
        return value;
    }
}
