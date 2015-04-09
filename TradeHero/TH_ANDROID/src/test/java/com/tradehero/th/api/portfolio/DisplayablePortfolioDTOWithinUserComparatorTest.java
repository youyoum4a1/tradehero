package com.tradehero.th.api.portfolio;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
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
