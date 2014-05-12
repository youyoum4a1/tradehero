package com.tradehero.th.api.leaderboard.key;


public class BasePagedLeaderboardKeyTest extends BaseLeaderboardKeyTest
{
    public static final String TAG = BasePagedLeaderboardKeyTest.class.getSimpleName();

    protected PagedLeaderboardKey getPaged2Key1()
    {
        return new PagedLeaderboardKey(1, 2);
    }

    protected PagedLeaderboardKey getPaged3Key1()
    {
        return new PagedLeaderboardKey(1, 3);
    }

    protected PagedLeaderboardKey getPaged3Key2()
    {
        return new PagedLeaderboardKey(2, 3);
    }

    protected PagedLeaderboardKey getPaged4Key2()
    {
        return new PagedLeaderboardKey(2, 4);
    }
}
