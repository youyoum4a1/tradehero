package com.tradehero.th.api.leaderboard.key;

/**
 * Created by xavier on 2/12/14.
 */
public class BasePerPagedLeaderboardKeyTest extends BasePagedLeaderboardKeyTest
{
    public static final String TAG = BasePerPagedLeaderboardKeyTest.class.getSimpleName();

    protected PerPagedLeaderboardKey getPer5Paged3Key1()
    {
        return new PerPagedLeaderboardKey(1, 3, 5);
    }

    protected PerPagedLeaderboardKey getPer5Paged4Key1()
    {
        return new PerPagedLeaderboardKey(1, 4, 5);
    }

    protected PerPagedLeaderboardKey getPer6Paged3Key1()
    {
        return new PerPagedLeaderboardKey(1, 3, 6);
    }

    protected PerPagedLeaderboardKey getPer6Paged4Key1()
    {
        return new PerPagedLeaderboardKey(1, 4, 6);
    }

    protected PerPagedLeaderboardKey getPer5Paged3Key2()
    {
        return new PerPagedLeaderboardKey(2, 3, 5);
    }

    protected PerPagedLeaderboardKey getPer5Paged4Key2()
    {
        return new PerPagedLeaderboardKey(2, 4, 5);
    }

    protected PerPagedLeaderboardKey getPer6Paged3Key2()
    {
        return new PerPagedLeaderboardKey(2, 3, 6);
    }

    protected PerPagedLeaderboardKey getPer6Paged4Key2()
    {
        return new PerPagedLeaderboardKey(2, 4, 6);
    }
}
