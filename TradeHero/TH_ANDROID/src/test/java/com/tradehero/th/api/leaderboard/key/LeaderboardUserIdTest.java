package com.ayondo.academy.api.leaderboard.key;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LeaderboardUserIdTest extends LeaderboardUserIdTestBase
{
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
