package com.tradehero.th.models.position;

import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.position.GetPositionsDTO;
import org.jetbrains.annotations.NotNull;
import rx.functions.Func1;

public class DTOProcessorGetPositions implements Func1<GetPositionsDTO, GetPositionsDTO>
{
    @NotNull private final LeaderboardMarkUserId leaderboardMarkUserId;

    public DTOProcessorGetPositions(@NotNull LeaderboardMarkUserId leaderboardMarkUserId)
    {
        this.leaderboardMarkUserId = leaderboardMarkUserId;
    }

    @Override public GetPositionsDTO call(@NotNull GetPositionsDTO value)
    {
        value.setOnInPeriod(leaderboardMarkUserId);
        return value;
    }
}
