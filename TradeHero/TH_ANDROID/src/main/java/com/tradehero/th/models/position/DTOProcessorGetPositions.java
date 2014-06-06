package com.tradehero.th.models.position;

import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.models.DTOProcessor;

public class DTOProcessorGetPositions implements DTOProcessor<GetPositionsDTO>
{
    private final LeaderboardMarkUserId leaderboardMarkUserId;

    public DTOProcessorGetPositions(LeaderboardMarkUserId leaderboardMarkUserId)
    {
        this.leaderboardMarkUserId = leaderboardMarkUserId;
    }

    @Override public GetPositionsDTO process(GetPositionsDTO value)
    {
        if (value != null)
        {
            value.setOnInPeriod(leaderboardMarkUserId);
        }
        return value;
    }
}
