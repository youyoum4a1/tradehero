package com.tradehero.th.api.leaderboard.key;

import com.tradehero.THRobolectricTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(THRobolectricTestRunner.class)
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
        assertTrue(new MostSkilledLeaderboardDefListKey(1).equals(new MostSkilledLeaderboardDefListKey(1)));
        assertEquals(new MostSkilledLeaderboardDefListKey(1), new MostSkilledLeaderboardDefListKey(1));

        assertTrue(new SectorLeaderboardDefListKey(1).equals(new SectorLeaderboardDefListKey(1)));
        assertEquals(new SectorLeaderboardDefListKey(1), new SectorLeaderboardDefListKey(1));

        assertTrue(new ExchangeLeaderboardDefListKey(1).equals(new ExchangeLeaderboardDefListKey(1)));
        assertEquals(new ExchangeLeaderboardDefListKey(1), new ExchangeLeaderboardDefListKey(1));

        assertTrue(new TimePeriodLeaderboardDefListKey(1).equals(new TimePeriodLeaderboardDefListKey(1)));
        assertEquals(new TimePeriodLeaderboardDefListKey(1), new TimePeriodLeaderboardDefListKey(1));
    }

    @Test public void testNotEqualsOthers()
    {
        assertFalse(new MostSkilledLeaderboardDefListKey(1).equals(new SectorLeaderboardDefListKey(1)));

        assertFalse(new SectorLeaderboardDefListKey(1).equals(new ExchangeLeaderboardDefListKey(1)));

        assertFalse(new ExchangeLeaderboardDefListKey(1).equals(new TimePeriodLeaderboardDefListKey(1)));

        assertFalse(new TimePeriodLeaderboardDefListKey(1).equals(new ExchangeLeaderboardDefListKey(1)));
    }
}
