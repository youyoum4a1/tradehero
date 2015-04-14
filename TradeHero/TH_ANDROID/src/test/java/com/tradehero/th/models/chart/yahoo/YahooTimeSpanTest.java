package com.tradehero.th.models.chart.yahoo;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.models.chart.ChartTimeSpan;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
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
