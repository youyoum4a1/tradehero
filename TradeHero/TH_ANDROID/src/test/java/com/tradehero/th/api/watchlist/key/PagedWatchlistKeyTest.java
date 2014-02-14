package com.tradehero.th.api.watchlist.key;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 2/14/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PagedWatchlistKeyTest extends BasePagedWatchlistKeyTest
{
    public static final String TAG = PagedWatchlistKeyTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsToItself()
    {
        assertTrue(getPagedNull().equals(getPagedNull()));
        assertTrue(getPaged1().equals(getPaged1()));
        assertTrue(getPaged2().equals(getPaged2()));
    }
}
