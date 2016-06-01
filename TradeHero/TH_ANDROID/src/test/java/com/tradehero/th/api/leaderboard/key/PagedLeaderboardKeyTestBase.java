package com.ayondo.academy.api.leaderboard.key;

public class PagedLeaderboardKeyTestBase extends LeaderboardKeyTestBase
{
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
