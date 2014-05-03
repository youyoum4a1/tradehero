package com.tradehero.th.api.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DisplayablePortfolioDTOWithinUserComparatorTest
{
    public static final String TAG = DisplayablePortfolioDTOWithinUserComparatorTest.class.getSimpleName();

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
