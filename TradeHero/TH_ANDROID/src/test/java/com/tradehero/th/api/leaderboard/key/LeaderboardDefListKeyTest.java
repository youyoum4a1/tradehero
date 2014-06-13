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
        assertTrue(new MostSkilledLeaderboardDefListKey().equals(new MostSkilledLeaderboardDefListKey()));
        assertEquals(new MostSkilledLeaderboardDefListKey(), new MostSkilledLeaderboardDefListKey());

        assertTrue(new SectorLeaderboardDefListKey().equals(new SectorLeaderboardDefListKey()));
        assertEquals(new SectorLeaderboardDefListKey(), new SectorLeaderboardDefListKey());

        assertTrue(new ExchangeLeaderboardDefListKey().equals(new ExchangeLeaderboardDefListKey()));
        assertEquals(new ExchangeLeaderboardDefListKey(), new ExchangeLeaderboardDefListKey());

        assertTrue(new TimePeriodLeaderboardDefListKey().equals(new TimePeriodLeaderboardDefListKey()));
        assertEquals(new TimePeriodLeaderboardDefListKey(), new TimePeriodLeaderboardDefListKey());
    }

    @Test public void testNotEqualsOthers()
    {
        assertFalse(new MostSkilledLeaderboardDefListKey().equals(new SectorLeaderboardDefListKey()));

        assertFalse(new SectorLeaderboardDefListKey().equals(new ExchangeLeaderboardDefListKey()));

        assertFalse(new ExchangeLeaderboardDefListKey().equals(new TimePeriodLeaderboardDefListKey()));

        assertFalse(new TimePeriodLeaderboardDefListKey().equals(new ExchangeLeaderboardDefListKey()));
    }
}
