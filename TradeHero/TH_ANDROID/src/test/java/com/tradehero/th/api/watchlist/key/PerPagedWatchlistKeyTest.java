package com.tradehero.th.api.watchlist.key;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 2/14/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PerPagedWatchlistKeyTest extends BasePerPagedWatchlistKeyTest
{
    public static final String TAG = PerPagedWatchlistKeyTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsToItself()
    {
        assertTrue(getPagedNullPerPagedNull().equals(getPagedNullPerPagedNull()));
        assertTrue(getPagedNullPerPaged3().equals(getPagedNullPerPaged3()));
        assertTrue(getPagedNullPerPaged4().equals(getPagedNullPerPaged4()));

        assertTrue(getPaged1PerPagedNull().equals(getPaged1PerPagedNull()));
        assertTrue(getPaged1PerPaged3().equals(getPaged1PerPaged3()));
        assertTrue(getPaged1PerPaged4().equals(getPaged1PerPaged4()));

        assertTrue(getPaged2PerPagedNull().equals(getPaged2PerPagedNull()));
        assertTrue(getPaged2PerPaged3().equals(getPaged2PerPaged3()));
        assertTrue(getPaged2PerPaged4().equals(getPaged2PerPaged4()));
    }

    @Test public void testPagedNotEqualsPerPaged()
    {
        assertFalse(getPagedNull().equals(getPagedNullPerPagedNull()));
        assertFalse(getPagedNull().equals(getPagedNullPerPaged3()));
        assertFalse(getPagedNull().equals(getPagedNullPerPaged4()));

        assertFalse(getPaged1().equals(getPaged1PerPagedNull()));
        assertFalse(getPaged1().equals(getPaged1PerPaged3()));
        assertFalse(getPaged1().equals(getPaged1PerPaged4()));

        assertFalse(getPaged2().equals(getPaged2PerPagedNull()));
        assertFalse(getPaged2().equals(getPaged2PerPaged3()));
        assertFalse(getPaged2().equals(getPaged2PerPaged4()));
    }

    @Test public void testPerPagedNotEqualsPaged()
    {
        assertFalse(getPagedNullPerPagedNull().equals(getPagedNull()));
        assertFalse(getPagedNullPerPaged3().equals(getPagedNull()));
        assertFalse(getPagedNullPerPaged4().equals(getPagedNull()));

        assertFalse(getPaged1PerPagedNull().equals(getPaged1()));
        assertFalse(getPaged1PerPaged3().equals(getPaged1()));
        assertFalse(getPaged1PerPaged4().equals(getPaged1()));

        assertFalse(getPaged2PerPagedNull().equals(getPaged2()));
        assertFalse(getPaged2PerPaged3().equals(getPaged2()));
        assertFalse(getPaged2PerPaged4().equals(getPaged2()));
    }

    @Test public void testPageChangePreventsEquals()
    {
        assertFalse(getPagedNullPerPagedNull().equals(getPaged1PerPagedNull()));
        assertFalse(getPagedNullPerPagedNull().equals(getPaged2PerPagedNull()));
        assertFalse(getPagedNullPerPaged3().equals(getPaged1PerPaged3()));
        assertFalse(getPagedNullPerPaged3().equals(getPaged2PerPaged3()));
        assertFalse(getPagedNullPerPaged4().equals(getPaged1PerPaged4()));
        assertFalse(getPagedNullPerPaged4().equals(getPaged2PerPaged4()));

        assertFalse(getPaged1PerPagedNull().equals(getPagedNullPerPagedNull()));
        assertFalse(getPaged1PerPagedNull().equals(getPaged2PerPagedNull()));
        assertFalse(getPaged1PerPaged3().equals(getPagedNullPerPaged3()));
        assertFalse(getPaged1PerPaged3().equals(getPaged2PerPaged3()));
        assertFalse(getPaged1PerPaged4().equals(getPagedNullPerPaged4()));
        assertFalse(getPaged1PerPaged4().equals(getPaged2PerPaged4()));

        assertFalse(getPaged2PerPagedNull().equals(getPagedNullPerPagedNull()));
        assertFalse(getPaged2PerPagedNull().equals(getPaged1PerPagedNull()));
        assertFalse(getPaged2PerPaged3().equals(getPagedNullPerPaged3()));
        assertFalse(getPaged2PerPaged3().equals(getPaged1PerPaged3()));
        assertFalse(getPaged2PerPaged4().equals(getPagedNullPerPaged4()));
        assertFalse(getPaged2PerPaged4().equals(getPaged1PerPaged4()));
    }

    @Test public void testPerPageChangePreventsEquals()
    {
        assertFalse(getPagedNullPerPagedNull().equals(getPagedNullPerPaged3()));
        assertFalse(getPagedNullPerPagedNull().equals(getPagedNullPerPaged4()));
        assertFalse(getPagedNullPerPaged3().equals(getPagedNullPerPagedNull()));
        assertFalse(getPagedNullPerPaged3().equals(getPagedNullPerPaged4()));
        assertFalse(getPagedNullPerPaged4().equals(getPagedNullPerPagedNull()));
        assertFalse(getPagedNullPerPaged4().equals(getPagedNullPerPaged3()));

        assertFalse(getPaged1PerPagedNull().equals(getPaged1PerPaged3()));
        assertFalse(getPaged1PerPagedNull().equals(getPaged1PerPaged4()));
        assertFalse(getPaged1PerPaged3().equals(getPaged1PerPagedNull()));
        assertFalse(getPaged1PerPaged3().equals(getPaged1PerPaged4()));
        assertFalse(getPaged1PerPaged4().equals(getPaged1PerPagedNull()));
        assertFalse(getPaged1PerPaged4().equals(getPaged1PerPaged3()));

        assertFalse(getPaged2PerPagedNull().equals(getPaged2PerPaged3()));
        assertFalse(getPaged2PerPagedNull().equals(getPaged2PerPaged4()));
        assertFalse(getPaged2PerPaged3().equals(getPaged2PerPagedNull()));
        assertFalse(getPaged2PerPaged3().equals(getPaged2PerPaged4()));
        assertFalse(getPaged2PerPaged4().equals(getPaged2PerPagedNull()));
        assertFalse(getPaged2PerPaged4().equals(getPaged2PerPaged3()));
    }
}
