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
public class PagedLeaderboardKeyTest extends PagedLeaderboardKeyTestBase
{
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertTrue(getPaged2Key1().equals(getPaged2Key1()));
        assertEquals(getPaged2Key1(), getPaged2Key1());
    }

    @Test public void testClassMakesADifference()
    {
        assertFalse(getKey1().equals(getPaged2Key1()));
        assertFalse(getPaged2Key1().equals(getKey1()));
    }

    @Test public void testKeyMakesADifference()
    {
        assertFalse(getPaged3Key1().equals(getPaged3Key2()));
        assertFalse(getPaged2Key1().equals(getKey2()));
        assertFalse(getKey2().equals(getPaged3Key2()));
    }

    @Test public void testPageMakesADifference()
    {
        assertFalse(getPaged2Key1().equals(getPaged3Key1()));
        assertFalse(getPaged3Key2().equals(getPaged4Key2()));
    }
}
