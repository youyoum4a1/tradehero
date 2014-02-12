package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardDefKey;

/**
 * Created by xavier on 2/12/14.
 */
public class BasePerPagedLeaderboardDefKeyTest extends BasePagedLeaderboardDefKeyTest
{
    public static final String TAG = BasePerPagedLeaderboardDefKeyTest.class.getSimpleName();

    protected PerPagedLeaderboardDefKey getPer5Paged3DefKey1()
    {
        return new PerPagedLeaderboardDefKey(1, 3, 5);
    }

    protected PerPagedLeaderboardDefKey getPer5Paged4DefKey1()
    {
        return new PerPagedLeaderboardDefKey(1, 4, 5);
    }

    protected PerPagedLeaderboardDefKey getPer6Paged3DefKey1()
    {
        return new PerPagedLeaderboardDefKey(1, 3, 6);
    }

    protected PerPagedLeaderboardDefKey getPer6Paged4DefKey1()
    {
        return new PerPagedLeaderboardDefKey(1, 4, 6);
    }

    protected PerPagedLeaderboardDefKey getPer5Paged3DefKey2()
    {
        return new PerPagedLeaderboardDefKey(2, 3, 5);
    }

    protected PerPagedLeaderboardDefKey getPer5Paged4DefKey2()
    {
        return new PerPagedLeaderboardDefKey(2, 4, 5);
    }

    protected PerPagedLeaderboardDefKey getPer6Paged3DefKey2()
    {
        return new PerPagedLeaderboardDefKey(2, 3, 6);
    }

    protected PerPagedLeaderboardDefKey getPer6Paged4DefKey2()
    {
        return new PerPagedLeaderboardDefKey(2, 4, 6);
    }
}
