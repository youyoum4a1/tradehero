package com.tradehero.th.api.watchlist.key;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class PagedWatchlistKeyTest extends BasePagedWatchlistKeyTest
{
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
