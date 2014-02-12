package com.tradehero.th.api.leaderboard;

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
 * Created by xavier on 2/12/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PagedLeaderboardDefKeyTest extends BasePagedLeaderboardDefKeyTest
{
    public static final String TAG = PagedLeaderboardDefKeyTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertTrue(getPaged2DefKey1().equals(getPaged2DefKey1()));
        assertEquals(getPaged2DefKey1(), getPaged2DefKey1());
    }

    @Test public void testClassMakesADifference()
    {
        assertFalse(getDefKey1().equals(getPaged2DefKey1()));
        assertFalse(getPaged2DefKey1().equals(getDefKey1()));
    }

    @Test public void testDefKeyMakesADifference()
    {
        assertFalse(getPaged3DefKey1().equals(getPaged3DefKey2()));
        assertFalse(getPaged2DefKey1().equals(getDefKey2()));
        assertFalse(getDefKey2().equals(getPaged3DefKey2()));
    }

    @Test public void testPageMakesADifference()
    {
        assertFalse(getPaged2DefKey1().equals(getPaged3DefKey1()));
        assertFalse(getPaged3DefKey2().equals(getPaged4DefKey2()));
    }
}
