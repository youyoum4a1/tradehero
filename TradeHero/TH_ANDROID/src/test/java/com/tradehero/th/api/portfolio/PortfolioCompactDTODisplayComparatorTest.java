package com.tradehero.th.api.portfolio;

import com.tradehero.RobolectricMavenTestRunner;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class PortfolioCompactDTODisplayComparatorTest
{
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    private PortfolioCompactDTO getFirstDefaultPortfolio()
    {
        PortfolioCompactDTO portfolioDTO = new PortfolioCompactDTO();
        portfolioDTO.id = 1;
        portfolioDTO.providerId = null;
        portfolioDTO.isWatchlist = false;
        return portfolioDTO;
    }

    private boolean isFirstDefaultPortfolio(PortfolioCompactDTO portfolio)
    {
        return portfolio.id == 1 && portfolio.providerId == null && !portfolio.isWatchlist;
    }

    private PortfolioCompactDTO getSecondDefaultPortfolio()
    {
        PortfolioCompactDTO portfolioDTO = new PortfolioCompactDTO();
        portfolioDTO.id = 2;
        portfolioDTO.providerId = null;
        portfolioDTO.isWatchlist = false;
        return portfolioDTO;
    }

    private boolean isSecondDefaultPortfolio(PortfolioCompactDTO portfolio)
    {
        return portfolio.id == 2 && portfolio.providerId == null && !portfolio.isWatchlist;
    }

    private PortfolioCompactDTO getFirstWatchlistPortfolio()
    {
        PortfolioCompactDTO portfolioDTO = new PortfolioCompactDTO();
        portfolioDTO.id = 1;
        portfolioDTO.providerId = null;
        portfolioDTO.isWatchlist = true;
        return portfolioDTO;
    }

    private boolean isFirstWatchlistPortfolio(PortfolioCompactDTO portfolio)
    {
        return portfolio.id == 1 && portfolio.providerId == null && portfolio.isWatchlist;
    }

    private PortfolioCompactDTO getSecondWatchlistPortfolio()
    {
        PortfolioCompactDTO portfolioDTO = new PortfolioCompactDTO();
        portfolioDTO.id = 2;
        portfolioDTO.providerId = null;
        portfolioDTO.isWatchlist = true;
        return portfolioDTO;
    }

    private boolean isSecondWatchlistPortfolio(PortfolioCompactDTO portfolio)
    {
        return portfolio.id == 2 && portfolio.providerId == null && portfolio.isWatchlist;
    }

    private PortfolioCompactDTO getFirstCompetition1Portfolio()
    {
        PortfolioCompactDTO portfolioDTO = new PortfolioCompactDTO();
        portfolioDTO.id = 1;
        portfolioDTO.providerId = 123;
        portfolioDTO.isWatchlist = false;
        return portfolioDTO;
    }

    private boolean isFirstCompetition1Portfolio(PortfolioCompactDTO portfolio)
    {
        return portfolio.id == 1 && portfolio.providerId.equals(123) && !portfolio.isWatchlist;
    }

    private PortfolioCompactDTO getSecondCompetition1Portfolio()
    {
        PortfolioCompactDTO portfolioDTO = new PortfolioCompactDTO();
        portfolioDTO.id = 2;
        portfolioDTO.providerId = 123;
        portfolioDTO.isWatchlist = false;
        return portfolioDTO;
    }

    private boolean isSecondCompetition1Portfolio(PortfolioCompactDTO portfolio)
    {
        return portfolio.id == 2 && portfolio.providerId.equals(123) && !portfolio.isWatchlist;
    }

    private PortfolioCompactDTO getFirstCompetition2Portfolio()
    {
        PortfolioCompactDTO portfolioDTO = new PortfolioCompactDTO();
        portfolioDTO.id = 1;
        portfolioDTO.providerId = 456;
        portfolioDTO.isWatchlist = false;
        return portfolioDTO;
    }

    private boolean isFirstCompetition2Portfolio(PortfolioCompactDTO portfolio)
    {
        return portfolio.id == 1 && portfolio.providerId.equals(456) && !portfolio.isWatchlist;
    }

    private PortfolioCompactDTO getSecondCompetition2Portfolio()
    {
        PortfolioCompactDTO portfolioDTO = new PortfolioCompactDTO();
        portfolioDTO.id = 2;
        portfolioDTO.providerId = 456;
        portfolioDTO.isWatchlist = false;
        return portfolioDTO;
    }

    private boolean isSecondCompetition2Portfolio(PortfolioCompactDTO portfolio)
    {
        return portfolio.id == 2 && portfolio.providerId.equals(456) && !portfolio.isWatchlist;
    }

    private PortfolioCompactDTO getFirstMongrel1Portfolio()
    {
        PortfolioCompactDTO portfolioDTO = new PortfolioCompactDTO();
        portfolioDTO.id = 1;
        portfolioDTO.providerId = 123;
        portfolioDTO.isWatchlist = true;
        return portfolioDTO;
    }

    private boolean isFirstMongrel1Portfolio(PortfolioCompactDTO portfolio)
    {
        return portfolio.id == 1 && portfolio.providerId.equals(123) && portfolio.isWatchlist;
    }

    private PortfolioCompactDTO getSecondMongrel1Portfolio()
    {
        PortfolioCompactDTO portfolioDTO = new PortfolioCompactDTO();
        portfolioDTO.id = 2;
        portfolioDTO.providerId = 123;
        portfolioDTO.isWatchlist = true;
        return portfolioDTO;
    }

    private boolean isSecondMongrel1Portfolio(PortfolioCompactDTO portfolio)
    {
        return portfolio.id == 2 && portfolio.providerId.equals(123) && portfolio.isWatchlist;
    }

    @Test public void gotTheTestsRight()
    {
        assertTrue(isFirstDefaultPortfolio(getFirstDefaultPortfolio()));
        assertTrue(isSecondDefaultPortfolio(getSecondDefaultPortfolio()));
        assertTrue(isFirstWatchlistPortfolio(getFirstWatchlistPortfolio()));
        assertTrue(isSecondWatchlistPortfolio(getSecondWatchlistPortfolio()));
        assertTrue(isFirstCompetition1Portfolio(getFirstCompetition1Portfolio()));
        assertTrue(isSecondCompetition1Portfolio(getSecondCompetition1Portfolio()));
        assertTrue(isFirstCompetition2Portfolio(getFirstCompetition2Portfolio()));
        assertTrue(isSecondCompetition2Portfolio(getSecondCompetition2Portfolio()));
        assertTrue(isFirstMongrel1Portfolio(getFirstMongrel1Portfolio()));
        assertTrue(isSecondMongrel1Portfolio(getSecondMongrel1Portfolio()));
    }

    @Test public void getExpectedOrder()
    {
        SortedSet<PortfolioCompactDTO> set = new TreeSet<>(new PortfolioCompactDTODisplayComparator());

        set.add(getSecondWatchlistPortfolio());
        set.add(getFirstWatchlistPortfolio());
        set.add(getFirstCompetition2Portfolio());
        set.add(getSecondCompetition2Portfolio());
        set.add(getFirstDefaultPortfolio());
        set.add(getSecondDefaultPortfolio());
        set.add(getSecondMongrel1Portfolio());
        set.add(getFirstMongrel1Portfolio());
        set.add(null);
        set.add(getFirstCompetition1Portfolio());
        set.add(getSecondCompetition1Portfolio());

        assertEquals(11, set.size());
        Iterator<PortfolioCompactDTO> iterator = set.iterator();

        assertTrue(isFirstDefaultPortfolio(iterator.next()));
        assertTrue(isSecondDefaultPortfolio(iterator.next()));
        assertTrue(isFirstCompetition1Portfolio(iterator.next()));
        assertTrue(isSecondCompetition1Portfolio(iterator.next()));
        assertTrue(isFirstCompetition2Portfolio(iterator.next()));
        assertTrue(isSecondCompetition2Portfolio(iterator.next()));
        assertTrue(isFirstWatchlistPortfolio(iterator.next()));
        assertTrue(isSecondWatchlistPortfolio(iterator.next()));
        assertTrue(isFirstMongrel1Portfolio(iterator.next()));
        assertTrue(isSecondMongrel1Portfolio(iterator.next()));
        assertNull(iterator.next());

        //PortfolioCompactDTO element = iterator.next();
        //System.out.print(element.toString());
        //assertTrue(isSecondMongrel1Portfolio(element));

        //element = iterator.next();
        //assertNull(element.providerId);
        //assertTrue(element.isWatchlist);
        //
        //element = iterator.next();
        //assertNotNull(element.providerId);
        //assertTrue(element.isWatchlist);
        //
        //element = iterator.next();
        //assertNull(element);
    }
}
