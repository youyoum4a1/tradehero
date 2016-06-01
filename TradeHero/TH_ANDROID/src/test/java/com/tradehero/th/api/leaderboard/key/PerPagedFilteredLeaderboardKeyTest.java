package com.ayondo.academy.api.leaderboard.key;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PerPagedFilteredLeaderboardKeyTest extends PerPagedFilteredLeaderboardKeyTestBase
{
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testPutAllParameters()
    {
        PerPagedFilteredLeaderboardKey key = new PerPagedFilteredLeaderboardKey(1, 2, 3, 4.3f, 5.6f, 6.7f, 8.9f, 0.1f);
        Set<String> set = new LinkedHashSet<>();
        key.putParameters(set);
        assertEquals(8, set.size());
        Iterator<String> iterator = set.iterator();
        String value = iterator.next();
        assertEquals("id:1", value);
        value = iterator.next();
        assertEquals("page:2", value);
        value = iterator.next();
        assertEquals("perPage:3", value);
        value = iterator.next();
        assertEquals("winRatio:4.3", value);
        value = iterator.next();
        assertEquals("averageMonthlyTradeCount:5.6", value);
        value = iterator.next();
        assertEquals("averageHoldingDays:6.7", value);
        value = iterator.next();
        assertEquals("minSharpeRatio:8.9", value);
        value = iterator.next();
        assertEquals("minConsistency:0.1", value);
    }

    @Test public void testPutParametersOnly4()
    {
        PerPagedFilteredLeaderboardKey key = new PerPagedFilteredLeaderboardKey(1, 2, 3, null, 5.6f, 6.7f, 8.9f, 0.1f);
        Set<String> set = new LinkedHashSet<>();
        key.putParameters(set);
        assertEquals(7, set.size());
        Iterator<String> iterator = set.iterator();
        String value = iterator.next();
        assertEquals("id:1", value);
        value = iterator.next();
        assertEquals("page:2", value);
        value = iterator.next();
        assertEquals("perPage:3", value);
        value = iterator.next();
        assertEquals("averageMonthlyTradeCount:5.6", value);
        value = iterator.next();
        assertEquals("averageHoldingDays:6.7", value);
        value = iterator.next();
        assertEquals("minSharpeRatio:8.9", value);
        value = iterator.next();
        assertEquals("minConsistency:0.1", value);
    }

    @Test public void testContructFromStringSet()
    {
        PerPagedFilteredLeaderboardKey key = new PerPagedFilteredLeaderboardKey(1, 2, 3, 4.3f, 5.6f, 6.7f, 8.9f, 0.1f);
        Set<String> set = key.getFilterStringSet();
        PerPagedFilteredLeaderboardKey recreated = new PerPagedFilteredLeaderboardKey(set, null);
        System.out.println("winRatio " + recreated.winRatio);
        assertTrue(recreated.winRatio.equals(4.3f));
        assertTrue(recreated.averageMonthlyTradeCount.equals(5.6f));
        assertTrue(recreated.averageHoldingDays.equals(6.7f));
        assertTrue(recreated.minSharpeRatio.equals(8.9f));
        assertTrue(recreated.minConsistency.equals(0.1f));
    }
}
