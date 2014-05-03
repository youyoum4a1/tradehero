package com.tradehero.th.api.leaderboard.key;


abstract public class BaseLeaderboardKeyTest
{
    public static final String TAG = BaseLeaderboardKeyTest.class.getSimpleName();

    protected LeaderboardKey getKey1()
    {
        return new LeaderboardKey(1);
    }

    protected LeaderboardKey getKey2()
    {
        return new LeaderboardKey(2);
    }
}
