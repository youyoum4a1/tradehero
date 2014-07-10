package com.tradehero.th.api.portfolio;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class DisplayablePortfolioDTOWithinUserComparatorTest
{
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testNullComparisons()
    {
        DisplayablePortfolioDTOWithinUserComparator comparator = new DisplayablePortfolioDTOWithinUserComparator();
        assertEquals(1, comparator.compare(null, new DisplayablePortfolioDTO()));
        assertEquals(-1, comparator.compare(new DisplayablePortfolioDTO(), null));
    }
}
