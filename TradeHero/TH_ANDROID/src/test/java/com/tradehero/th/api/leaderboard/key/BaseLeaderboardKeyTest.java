package com.tradehero.th.api.leaderboard.key;

abstract public class BaseLeaderboardKeyTest
{
    protected LeaderboardKey getKey1()
    {
        return new LeaderboardKey(1);
    }

    protected LeaderboardKey getKey2()
    {
        return new LeaderboardKey(2);
    }
}
