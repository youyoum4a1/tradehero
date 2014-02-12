package com.tradehero.th.api.leaderboard.key;

/**
 * Created by xavier on 2/12/14.
 */
public class BasePerPagedFilteredLeaderboardKeyTest extends BasePerPagedLeaderboardKeyTest
{
    public static final String TAG = BasePerPagedFilteredLeaderboardKeyTest.class.getSimpleName();

    protected PerPagedFilteredLeaderboardKey getWin7Per5Paged3DefKey1()
    {
        return new PerPagedFilteredLeaderboardKey(1, 3, 5, (float) 7, null, null, null, null);
    }

    protected PerPagedFilteredLeaderboardKey getWin7Per5Paged4DefKey1()
    {
        return new PerPagedFilteredLeaderboardKey(1, 4, 5, (float) 7, null, null, null, null);
    }

    protected PerPagedFilteredLeaderboardKey getWin7Per6Paged3DefKey1()
    {
        return new PerPagedFilteredLeaderboardKey(1, 3, 6, (float) 7, null, null, null, null);
    }

    protected PerPagedFilteredLeaderboardKey getWin7Per6Paged4DefKey1()
    {
        return new PerPagedFilteredLeaderboardKey(1, 4, 6, (float) 7, null, null, null, null);
    }

    protected PerPagedFilteredLeaderboardKey getWin7Per5Paged3DefKey2()
    {
        return new PerPagedFilteredLeaderboardKey(2, 3, 5, (float) 7, null, null, null, null);
    }

    protected PerPagedFilteredLeaderboardKey getWin7Per5Paged4DefKey2()
    {
        return new PerPagedFilteredLeaderboardKey(2, 4, 5, (float) 7, null, null, null, null);
    }

    protected PerPagedFilteredLeaderboardKey getWin7Per6Paged3DefKey2()
    {
        return new PerPagedFilteredLeaderboardKey(2, 3, 6, (float) 7, null, null, null, null);
    }

    protected PerPagedFilteredLeaderboardKey getWin7Per6Paged4DefKey2()
    {
        return new PerPagedFilteredLeaderboardKey(2, 4, 6, (float) 7, null, null, null, null);
    }
}
