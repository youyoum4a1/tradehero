package com.tradehero.th.api.leaderboard.key;

abstract public class BaseLeaderboardUserIdTest
{
    protected LeaderboardUserId getKey1()
    {
        return new LeaderboardUserId(11, 1l);
    }

    protected LeaderboardUserId getKey2()
    {
        return new LeaderboardUserId(12, 2l);
    }
}
