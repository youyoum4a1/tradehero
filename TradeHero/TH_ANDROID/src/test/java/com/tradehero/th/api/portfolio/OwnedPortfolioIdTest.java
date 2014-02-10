package com.tradehero.th.api.portfolio;

import com.tradehero.common.utils.THJsonAdapter;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 2/10/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class OwnedPortfolioIdTest
{
    public static final String TAG = OwnedPortfolioIdTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
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
