package com.ayondo.academy.api.portfolio;

import com.ayondo.academyRobolectricTestRunner;
import com.tradehero.common.utils.THJsonAdapter;
import com.ayondo.academy.BuildConfig;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OwnedPortfolioIdTest extends OwnedPorfolioIdTestBase
{
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testEqualsItself()
    {
        assertEquals(get1n2(), get1n2());
        assertTrue(get1n2().equals(get1n2()));

        assertEquals(get1n3(), get1n3());
        assertTrue(get1n3().equals(get1n3()));

        assertEquals(get3n2(), get3n2());
        assertTrue(get3n2().equals(get3n2()));
    }

    @Test public void testUserIdMatters()
    {
        assertFalse(get1n2().equals(get3n2()));
        assertFalse(get3n2().equals(get1n2()));
    }

    @Test public void testPortfolioIdMatters()
    {
        assertFalse(get1n2().equals(get1n3()));
        assertFalse(get1n3().equals(get1n2()));
    }

    @Test public void testSerialisedOwnedPortfolioId() throws IOException
    {
        OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(239284, 611105);
        String serialised = THJsonAdapter.getInstance().toStringBody(ownedPortfolioId);

        OwnedPortfolioId deserialised = (OwnedPortfolioId) THJsonAdapter.getInstance().fromBody(serialised, OwnedPortfolioId.class);
        assertTrue(deserialised.equals(ownedPortfolioId));
    }

    @Test public void testDeserialiseOwnedPortfolioId1()
    {
        String json = "{\"userId\":239284,\"portfolioId\":611105}";
        OwnedPortfolioId ownedPortfolioId = (OwnedPortfolioId) THJsonAdapter.getInstance().fromBody(json, OwnedPortfolioId.class);
        System.out.println(ownedPortfolioId);
        assertTrue(Integer.valueOf(239284).equals(ownedPortfolioId.userId));
        assertTrue(Integer.valueOf(611105).equals(ownedPortfolioId.portfolioId));
    }
}
