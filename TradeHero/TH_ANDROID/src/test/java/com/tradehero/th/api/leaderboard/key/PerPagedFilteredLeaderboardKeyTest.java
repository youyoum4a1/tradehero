package com.tradehero.th.api.leaderboard.key;

import java.util.Iterator;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 2/13/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PerPagedFilteredLeaderboardKeyTest extends BasePerPagedFilteredLeaderboardKeyTest
{
    public static final String TAG = PerPagedFilteredLeaderboardKeyTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testGetStringSetAll()
    {
        PerPagedFilteredLeaderboardKey key = new PerPagedFilteredLeaderboardKey(1, 2, 3, 4.3f, 5.6f, 6.7f, 8.9f, 0.1f);
        Set<String> set = key.getFilterStringSet();
        assertEquals(5, set.size());
        Iterator<String> iterator = set.iterator();
        String value = iterator.next();
        assertEquals("winRatio:4.3", value);
        value = iterator.next();
        assertEquals("maxPosRoiVolatility:0.1", value);
        value = iterator.next();
        assertEquals("averageHoldingDays:6.7", value);
        value = iterator.next();
        assertEquals("minSharpeRatio:8.9", value);
        value = iterator.next();
        assertEquals("averageMonthlyTradeCount:5.6", value);
    }

    @Test public void testGetStringSetOnly4()
    {
        PerPagedFilteredLeaderboardKey key = new PerPagedFilteredLeaderboardKey(1, 2, 3, null, 5.6f, 6.7f, 8.9f, 0.1f);
        Set<String> set = key.getFilterStringSet();
        assertEquals(4, set.size());
        Iterator<String> iterator = set.iterator();
        String value = iterator.next();
        assertEquals("maxPosRoiVolatility:0.1", value);
        value = iterator.next();
        assertEquals("averageHoldingDays:6.7", value);
        value = iterator.next();
        assertEquals("minSharpeRatio:8.9", value);
        value = iterator.next();
        assertEquals("averageMonthlyTradeCount:5.6", value);
    }

    @Test public void testContructFromStringSet()
    {
        PerPagedFilteredLeaderboardKey key = new PerPagedFilteredLeaderboardKey(1, 2, 3, 4.3f, 5.6f, 6.7f, 8.9f, 0.1f);
        Set<String> set = key.getFilterStringSet();
        PerPagedFilteredLeaderboardKey recreated = new PerPagedFilteredLeaderboardKey(set);
        System.out.println("winRatio " + recreated.winRatio);
        assertTrue(recreated.winRatio.equals(4.3f));
        assertTrue(recreated.averageMonthlyTradeCount.equals(5.6f));
        assertTrue(recreated.averageHoldingDays.equals(6.7f));
        assertTrue(recreated.minSharpeRatio.equals(8.9f));
        assertTrue(recreated.maxPosRoiVolatility.equals(0.1f));
    }
}
