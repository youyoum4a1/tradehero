package com.tradehero.th.api.leaderboard.key;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 2/10/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LeaderboardUserIdTest extends BaseLeaderboardUserIdTest
{
    public static final String TAG = LeaderboardUserIdTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertTrue(getKey1().equals(getKey1()));
        assertEquals(getKey1(), getKey1());

        assertTrue(getKey2().equals(getKey2()));
        assertEquals(getKey2(), getKey2());
    }

    @Test public void testEqualsValueMakesADifference()
    {
        assertFalse(getKey1().equals(getKey2()));
        assertFalse(getKey2().equals(getKey1()));
    }
}
