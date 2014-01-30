package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import java.util.Iterator;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 1/23/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class MenuOwnedPortfolioIdTest
{
    public static final String TAG = MenuOwnedPortfolioIdTest.class.getSimpleName();


    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void whenEquals()
    {
        MenuOwnedPortfolioId menuOwnedPortfolioId1 = new MenuOwnedPortfolioId(1, 2, "a");
        MenuOwnedPortfolioId menuOwnedPortfolioId2 = new MenuOwnedPortfolioId(1, 2, "a");
        assertTrue(menuOwnedPortfolioId1.equals(menuOwnedPortfolioId2));
        assertTrue(menuOwnedPortfolioId2.equals(menuOwnedPortfolioId1));
    }

    @Test public void whenNotEquals()
    {
        MenuOwnedPortfolioId menuOwnedPortfolioId1 = new MenuOwnedPortfolioId(1, 2, "a");
        MenuOwnedPortfolioId menuOwnedPortfolioId2 = new MenuOwnedPortfolioId(1, 2, "b");
        assertFalse(menuOwnedPortfolioId1.equals(menuOwnedPortfolioId2));
        assertFalse(menuOwnedPortfolioId2.equals(menuOwnedPortfolioId1));
    }

    @Test public void equalsOwnedPortfolioIdOneWayOnly()
    {
        MenuOwnedPortfolioId menuOwnedPortfolioId = new MenuOwnedPortfolioId(1, 2, "a");
        OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(1, 2);
        assertTrue(ownedPortfolioId.equals(menuOwnedPortfolioId));
        assertFalse(menuOwnedPortfolioId.equals(ownedPortfolioId));
    }
}
