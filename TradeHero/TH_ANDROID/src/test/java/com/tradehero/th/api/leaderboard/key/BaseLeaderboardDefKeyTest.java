package com.tradehero.th.api.leaderboard.key;

/**
 * Created by xavier on 2/12/14.
 */
abstract public class BaseLeaderboardDefKeyTest
{
    public static final String TAG = BaseLeaderboardDefKeyTest.class.getSimpleName();

    protected LeaderboardDefKey getDefKey1()
    {
        return new LeaderboardDefKey(1);
    }

    protected LeaderboardDefKey getDefKey2()
    {
        return new LeaderboardDefKey(2);
    }
}
