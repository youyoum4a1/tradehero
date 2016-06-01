package com.ayondo.academy.models.chart.yahoo;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.models.chart.ChartTimeSpan;
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
