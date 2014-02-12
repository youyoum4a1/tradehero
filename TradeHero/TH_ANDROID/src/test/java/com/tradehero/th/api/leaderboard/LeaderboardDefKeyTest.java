package com.tradehero.th.api.leaderboard;

import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import java.io.IOException;
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
public class LeaderboardDefKeyTest extends BaseLeaderboardDefKeyTest
{
    public static final String TAG = LeaderboardDefKeyTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertTrue(getDefKey1().equals(getDefKey1()));
        assertEquals(getDefKey1(), getDefKey1());

        assertTrue(getDefKey2().equals(getDefKey2()));
        assertEquals(getDefKey2(), getDefKey2());
    }

    @Test public void testEqualsDefMakesADifference()
    {
        assertFalse(getDefKey1().equals(getDefKey2()));
        assertFalse(getDefKey2().equals(getDefKey1()));
    }
}
