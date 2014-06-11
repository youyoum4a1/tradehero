package com.tradehero.th.api.leaderboard.key;

import com.tradehero.RobolectricMavenTestRunner;
import javax.inject.Inject;
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
    @Inject protected LeaderboardDefListKeyFactory leaderboardDefListKeyFactory;
    
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertTrue(leaderboardDefListKeyFactory.createMostSkilled().equals(leaderboardDefListKeyFactory.createMostSkilled()));
        assertEquals(leaderboardDefListKeyFactory.createMostSkilled(), leaderboardDefListKeyFactory.createMostSkilled());

        assertTrue(leaderboardDefListKeyFactory.createSector().equals(leaderboardDefListKeyFactory.createSector()));
        assertEquals(leaderboardDefListKeyFactory.createSector(), leaderboardDefListKeyFactory.createSector());

        assertTrue(leaderboardDefListKeyFactory.createExchange().equals(leaderboardDefListKeyFactory.createExchange()));
        assertEquals(leaderboardDefListKeyFactory.createExchange(), leaderboardDefListKeyFactory.createExchange());

        assertTrue(leaderboardDefListKeyFactory.createTimePeriod().equals(leaderboardDefListKeyFactory.createTimePeriod()));
        assertEquals(leaderboardDefListKeyFactory.createTimePeriod(), leaderboardDefListKeyFactory.createTimePeriod());
    }

    @Test public void testNotEqualsOthers()
    {
        assertFalse(leaderboardDefListKeyFactory.createMostSkilled().equals(leaderboardDefListKeyFactory.createSector()));

        assertFalse(leaderboardDefListKeyFactory.createSector().equals(leaderboardDefListKeyFactory.createExchange()));

        assertFalse(leaderboardDefListKeyFactory.createExchange().equals(leaderboardDefListKeyFactory.createTimePeriod()));

        assertFalse(leaderboardDefListKeyFactory.createTimePeriod().equals(leaderboardDefListKeyFactory.createExchange()));
    }
}
