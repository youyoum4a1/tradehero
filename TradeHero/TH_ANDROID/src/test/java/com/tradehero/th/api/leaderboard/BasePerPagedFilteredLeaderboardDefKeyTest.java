package com.tradehero.th.api.leaderboard;

/**
 * Created by xavier on 2/12/14.
 */
public class BasePerPagedFilteredLeaderboardDefKeyTest extends BasePerPagedLeaderboardDefKeyTest
{
    public static final String TAG = BasePerPagedFilteredLeaderboardDefKeyTest.class.getSimpleName();

    protected PerPagedFilteredLeaderboardDefKey getPer5Paged3DefKey1()
    {
        return new PerPagedFilteredLeaderboardDefKey(1, 3, 5);
    }

    protected PerPagedFilteredLeaderboardDefKey getPer5Paged4DefKey1()
    {
        return new PerPagedFilteredLeaderboardDefKey(1, 4, 5);
    }

    protected PerPagedFilteredLeaderboardDefKey getPer6Paged3DefKey1()
    {
        return new PerPagedFilteredLeaderboardDefKey(1, 3, 6);
    }

    protected PerPagedFilteredLeaderboardDefKey getPer6Paged4DefKey1()
    {
        return new PerPagedFilteredLeaderboardDefKey(1, 4, 6);
    }

    protected PerPagedFilteredLeaderboardDefKey getPer5Paged3DefKey2()
    {
        return new PerPagedFilteredLeaderboardDefKey(2, 3, 5);
    }

    protected PerPagedFilteredLeaderboardDefKey getPer5Paged4DefKey2()
    {
        return new PerPagedFilteredLeaderboardDefKey(2, 4, 5);
    }

    protected PerPagedFilteredLeaderboardDefKey getPer6Paged3DefKey2()
    {
        return new PerPagedFilteredLeaderboardDefKey(2, 3, 6);
    }

    protected PerPagedFilteredLeaderboardDefKey getPer6Paged4DefKey2()
    {
        return new PerPagedFilteredLeaderboardDefKey(2, 4, 6);
    }
}
