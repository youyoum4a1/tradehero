package com.tradehero.th.api.watchlist;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class WatchlistPositionDTOTest extends BaseWatchlistPositionDTOTest
{
    @Before @Override public void setUp()
    {
        super.setUp();
    }

    @After public void tearDown()
    {
    }

    @Test public void testCanCopyFieldsWithProperClass()
    {
        WatchlistPositionDTO first = new WatchlistPositionDTO();
        first.id = 1;
        first.averagePriceRefCcy = 2.0d;
        first.portfolioId = 3;
        first.shares = 4;
        first.userId = 5;
        first.securityId = 6;
        first.realizedPLRefCcy = 7d;
        first.unrealizedPLRefCcy = 8d;
        first.marketValueRefCcy = 9d;
        first.earliestTradeUtc = new Date(578573456);
        first.latestTradeUtc = new Date(32488945);
        first.sumInvestedAmountRefCcy = 10d;
        first.totalTransactionCostRefCcy = 11d;
        first.aggregateCount = 12;
        first.watchlistPrice = 13d;
        first.securityDTO = new SecurityCompactDTO();
        first.securityDTO.id = 14;
        WatchlistPositionDTO second = new WatchlistPositionDTO(first, WatchlistPositionDTO.class);
        assertTrue(haveSameFields(first, second));
        assertTrue(securityCompactDTOTest.haveSameFields(first.securityDTO, second.securityDTO));
    }
}
