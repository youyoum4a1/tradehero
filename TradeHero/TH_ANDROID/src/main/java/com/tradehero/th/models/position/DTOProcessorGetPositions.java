package com.ayondo.academy.models.position;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.leaderboard.position.LeaderboardMarkUserId;
import com.ayondo.academy.api.position.GetPositionsDTO;
import rx.functions.Func1;

public class DTOProcessorGetPositions implements Func1<GetPositionsDTO, GetPositionsDTO>
{
    @NonNull private final LeaderboardMarkUserId leaderboardMarkUserId;

    public DTOProcessorGetPositions(@NonNull LeaderboardMarkUserId leaderboardMarkUserId)
    {
        this.leaderboardMarkUserId = leaderboardMarkUserId;
    }

    @Override public GetPositionsDTO call(@NonNull GetPositionsDTO value)
    {
        value.setOnInPeriod(leaderboardMarkUserId);
        return value;
    }
}
