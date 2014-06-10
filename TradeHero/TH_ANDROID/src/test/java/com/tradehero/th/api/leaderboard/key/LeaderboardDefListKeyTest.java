package com.tradehero.th.api.leaderboard.key;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class LeaderboardDefListKeyTest
{
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertTrue(LeaderboardDefListKey.getMostSkilled().equals(LeaderboardDefListKey.getMostSkilled()));
        assertEquals(LeaderboardDefListKey.getMostSkilled(), LeaderboardDefListKey.getMostSkilled());

        assertTrue(LeaderboardDefListKey.getSector().equals(LeaderboardDefListKey.getSector()));
        assertEquals(LeaderboardDefListKey.getSector(), LeaderboardDefListKey.getSector());

        assertTrue(LeaderboardDefListKey.getExchange().equals(LeaderboardDefListKey.getExchange()));
        assertEquals(LeaderboardDefListKey.getExchange(), LeaderboardDefListKey.getExchange());

        assertTrue(LeaderboardDefListKey.getTimePeriod().equals(LeaderboardDefListKey.getTimePeriod()));
        assertEquals(LeaderboardDefListKey.getTimePeriod(), LeaderboardDefListKey.getTimePeriod());

        assertTrue(LeaderboardDefListKey.getCommunity().equals(LeaderboardDefListKey.getCommunity()));
        assertEquals(LeaderboardDefListKey.getCommunity(), LeaderboardDefListKey.getCommunity());
    }

    @Test public void testNotEqualsOthers()
    {
        assertFalse(LeaderboardDefListKey.getMostSkilled().equals(LeaderboardDefListKey.getSector()));

        assertFalse(LeaderboardDefListKey.getSector().equals(LeaderboardDefListKey.getExchange()));

        assertFalse(LeaderboardDefListKey.getExchange().equals(LeaderboardDefListKey.getTimePeriod()));

        assertFalse(LeaderboardDefListKey.getTimePeriod().equals(LeaderboardDefListKey.getCommunity()));

        assertFalse(LeaderboardDefListKey.getCommunity().equals(LeaderboardDefListKey.getMostSkilled()));
    }
}
