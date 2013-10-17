package com.tradehero.th.utils;

import android.content.Context;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

/** Created with IntelliJ IDEA. User: xavier Date: 10/17/13 Time: 4:29 PM To change this template use File | Settings | File Templates. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class NumberDisplayUtilsTest
{
    @Before public void setUp()
    {
        NumberDisplayUtils.context = Robolectric/*.application;*/.getShadowApplication().getApplicationContext();
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatWithRelevantDigits_shouldRejectFraction()
    {
        NumberDisplayUtils.formatWithRelevantDigits(0.1f, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatWithRelevantDigits_shouldRejectTooBig()
    {
        NumberDisplayUtils.formatWithRelevantDigits(1000000000000000d, 1);
    }

    @Test public void formatWithRelevantDigits_zero()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(0, 10), Matchers.equalTo("0"));
    }

    @Test public void formatWithRelevantDigits_10_1()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(10, 1), Matchers.equalTo("10"));
    }

    @Test public void formatWithRelevantDigits_10_2()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(10, 2), Matchers.equalTo("10"));
    }

    @Test public void formatWithRelevantDigits_10_3()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(10, 3), Matchers.equalTo("10.0"));
    }

    @Test public void formatWithRelevantDigits_15_1()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(15, 1), Matchers.equalTo("15"));
    }

    @Test public void formatWithRelevantDigits_15_2()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(15, 2), Matchers.equalTo("15"));
    }

    @Test public void formatWithRelevantDigits_15_3()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(15, 3), Matchers.equalTo("15.0"));
    }

    @Test public void formatWithRelevantDigits_1578_1()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(1578, 1), Matchers.equalTo("2k"));
    }

    @Test public void formatWithRelevantDigits_1578_2()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(1578, 2), Matchers.equalTo("1.6k"));
    }

    @Test public void formatWithRelevantDigits_1578_3()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(1578, 3), Matchers.equalTo("1.58k"));
    }

    @Test public void formatWithRelevantDigits_41578_1()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(41578, 1), Matchers.equalTo("42k"));
    }

    @Test public void formatWithRelevantDigits_41578_2()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(41578, 2), Matchers.equalTo("42k"));
    }

    @Test public void formatWithRelevantDigits_41578_3()
    {
        assertThat(NumberDisplayUtils.formatWithRelevantDigits(41578, 3), Matchers.equalTo("41.6k"));
    }
}
