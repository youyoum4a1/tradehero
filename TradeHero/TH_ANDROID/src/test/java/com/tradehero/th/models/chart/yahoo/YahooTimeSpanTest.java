package com.tradehero.th.models.chart.yahoo;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.models.chart.ChartTimeSpan;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class YahooTimeSpanTest
{
    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test public void returnsPeriodAbove1()
    {
        assertEquals(YahooTimeSpan.day1, YahooTimeSpan.getBestApproximation(new ChartTimeSpan(1)));
    }
}
