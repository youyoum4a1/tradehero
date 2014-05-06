package com.tradehero.th.models.chart.yahoo;

import com.tradehero.th.models.chart.ChartTimeSpan;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class YahooTimeSpanTest
{
    public static final String TAG = YahooTimeSpanTest.class.getSimpleName();

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
