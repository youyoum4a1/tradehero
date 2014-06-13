package com.tradehero.th.models.portfolio;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class MenuOwnedPortfolioIdTest
{
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
