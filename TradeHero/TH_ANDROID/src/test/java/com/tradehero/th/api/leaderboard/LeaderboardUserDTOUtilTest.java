package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.leaderboard.key.BaseLeaderboardUserIdTest;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LeaderboardUserDTOUtilTest extends BaseLeaderboardUserDTOUtilTest
{
    public static final String TAG = LeaderboardUserDTOUtilTest.class.getSimpleName();

    private LeaderboardUserDTOUtil leaderboardUserDTOUtil;

    @Before public void setUp()
    {
        this.leaderboardUserDTOUtil = new LeaderboardUserDTOUtil();
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertEmpty(getMapEmpty());
        assertMap1Item(getMap1Item());
        assertMap2Items(getMap2Items());
    }

    @Test public void testNullReturnsNull()
    {
        assertNull(leaderboardUserDTOUtil.map(null));
    }

    @Test public void testEmptyReturnsEmpty()
    {
        assertEmpty(leaderboardUserDTOUtil.map(new ArrayList<LeaderboardUserDTO>()));
    }

    @Test public void testList1ReturnsMap1()
    {
        assertMap1Item(leaderboardUserDTOUtil.map(getList1Item()));
    }

    @Test public void testList2ReturnsMap2()
    {
        assertMap2Items(leaderboardUserDTOUtil.map(getList2Items()));
    }
}
