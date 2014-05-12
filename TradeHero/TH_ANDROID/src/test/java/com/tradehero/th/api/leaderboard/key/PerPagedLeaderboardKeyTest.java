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


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PerPagedLeaderboardKeyTest extends BasePerPagedLeaderboardKeyTest
{
    public static final String TAG = PerPagedLeaderboardKeyTest.class.getSimpleName();
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertTrue(getPer5Paged3Key1().equals(getPer5Paged3Key1()));
        assertEquals(getPer5Paged3Key1(), getPer5Paged3Key1());
    }

    @Test public void testClassMakesADifference()
    {
        assertFalse(getKey1().equals(getPer5Paged3Key1()));
        assertFalse(getPer5Paged3Key1().equals(getKey1()));

        assertFalse(getPaged3Key1().equals(getPer5Paged3Key1()));
        assertFalse(getPer5Paged3Key1().equals(getPaged3Key1()));
    }

    @Test public void testKeyMakesADifference()
    {
        assertFalse(getPer5Paged3Key1().equals(getPer5Paged3Key2()));
        assertFalse(getPer5Paged3Key2().equals(getPer5Paged3Key1()));
    }

    @Test public void testPageMakesADifference()
    {
        assertFalse(getPer5Paged3Key1().equals(getPer5Paged4Key1()));
        assertFalse(getPer5Paged4Key1().equals(getPer5Paged3Key1()));
    }

    @Test public void testPerMakesADifference()
    {
        assertFalse(getPer5Paged3Key1().equals(getPer6Paged3Key1()));
        assertFalse(getPer6Paged3Key1().equals(getPer5Paged3Key1()));
    }
}
