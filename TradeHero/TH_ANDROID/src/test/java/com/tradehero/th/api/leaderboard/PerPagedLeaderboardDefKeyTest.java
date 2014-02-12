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
public class PerPagedLeaderboardDefKeyTest extends BasePerPagedLeaderboardDefKeyTest
{
    public static final String TAG = PerPagedLeaderboardDefKeyTest.class.getSimpleName();
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertTrue(getPer5Paged3DefKey1().equals(getPer5Paged3DefKey1()));
        assertEquals(getPer5Paged3DefKey1(), getPer5Paged3DefKey1());
    }

    @Test public void testClassMakesADifference()
    {
        assertFalse(getDefKey1().equals(getPer5Paged3DefKey1()));
        assertFalse(getPer5Paged3DefKey1().equals(getDefKey1()));

        assertFalse(getPaged3DefKey1().equals(getPer5Paged3DefKey1()));
        assertFalse(getPer5Paged3DefKey1().equals(getPaged3DefKey1()));
    }

    @Test public void testDefKeyMakesADifference()
    {
        assertFalse(getPer5Paged3DefKey1().equals(getPer5Paged3DefKey2()));
        assertFalse(getPer5Paged3DefKey2().equals(getPer5Paged3DefKey1()));
    }

    @Test public void testPageMakesADifference()
    {
        assertFalse(getPer5Paged3DefKey1().equals(getPer5Paged4DefKey1()));
        assertFalse(getPer5Paged4DefKey1().equals(getPer5Paged3DefKey1()));
    }

    @Test public void testPerMakesADifference()
    {
        assertFalse(getPer5Paged3DefKey1().equals(getPer6Paged3DefKey1()));
        assertFalse(getPer6Paged3DefKey1().equals(getPer5Paged3DefKey1()));
    }
}
