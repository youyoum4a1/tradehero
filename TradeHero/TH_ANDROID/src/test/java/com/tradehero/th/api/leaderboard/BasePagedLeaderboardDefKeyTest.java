package com.tradehero.th.api.leaderboard;

import org.junit.Test;

/**
 * Created by xavier on 2/12/14.
 */
public class BasePagedLeaderboardDefKeyTest extends BaseLeaderboardDefKeyTest
{
    public static final String TAG = BasePagedLeaderboardDefKeyTest.class.getSimpleName();

    protected PagedLeaderboardDefKey getPaged2DefKey1()
    {
        return new PagedLeaderboardDefKey(1, 2);
    }

    protected PagedLeaderboardDefKey getPaged3DefKey1()
    {
        return new PagedLeaderboardDefKey(1, 3);
    }

    protected PagedLeaderboardDefKey getPaged3DefKey2()
    {
        return new PagedLeaderboardDefKey(2, 3);
    }

    protected PagedLeaderboardDefKey getPaged4DefKey2()
    {
        return new PagedLeaderboardDefKey(2, 4);
    }
}
