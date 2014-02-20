package com.tradehero.th.api.leaderboard.key;

/**
 * Created by xavier on 2/12/14.
 */
abstract public class BaseLeaderboardUserIdTest
{
    public static final String TAG = BaseLeaderboardUserIdTest.class.getSimpleName();

    protected LeaderboardUserId getKey1()
    {
        return new LeaderboardUserId(1l);
    }

    protected LeaderboardUserId getKey2()
    {
        return new LeaderboardUserId(2l);
    }
}
