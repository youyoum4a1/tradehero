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
 * Created by xavier on 2/12/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SortedPerPagedLeaderboardDefKeyTest extends BaseSortedPerPagedLeaderboardDefKeyTest
{
    public static final String TAG = SortedPerPagedLeaderboardDefKeyTest.class.getSimpleName();
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertTrue(getSort7Per5Paged3DefKey1().equals(getSort7Per5Paged3DefKey1()));
        assertEquals(getSort7Per5Paged3DefKey1(), getSort7Per5Paged3DefKey1());
    }

    @Test public void testClassMakesADifference()
    {
        assertFalse(getDefKey1().equals(getSort7Per5Paged3DefKey1()));
        assertFalse(getSort7Per5Paged3DefKey1().equals(getDefKey1()));

        assertFalse(getPaged3DefKey1().equals(getSort7Per5Paged3DefKey1()));
        assertFalse(getSort7Per5Paged3DefKey1().equals(getPaged3DefKey1()));

        assertFalse(getPer5Paged3DefKey1().equals(getSort7Per5Paged3DefKey1()));
        assertFalse(getSort7Per5Paged3DefKey1().equals(getPer5Paged3DefKey1()));
    }

    @Test public void testDefKeyMakesADifference()
    {
        assertFalse(getSort7Per5Paged3DefKey1().equals(getSort7Per5Paged3DefKey2()));
        assertFalse(getSort7Per5Paged3DefKey2().equals(getSort7Per5Paged3DefKey1()));
    }

    @Test public void testPageMakesADifference()
    {
        assertFalse(getSort7Per5Paged3DefKey1().equals(getSort7Per5Paged4DefKey1()));
        assertFalse(getSort7Per5Paged4DefKey1().equals(getSort7Per5Paged3DefKey1()));
    }

    @Test public void testPerMakesADifference()
    {
        assertFalse(getSort7Per5Paged3DefKey1().equals(getSort7Per6Paged3DefKey1()));
        assertFalse(getSort7Per6Paged3DefKey1().equals(getSort7Per5Paged3DefKey1()));
    }

    @Test public void testSortMakesADifference()
    {
        assertFalse(getSort7Per5Paged3DefKey1().equals(getSort8Per5Paged3DefKey1()));
        assertFalse(getSort8Per5Paged3DefKey1().equals(getSort7Per5Paged3DefKey1()));
    }
}
