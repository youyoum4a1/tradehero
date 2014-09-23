package com.tradehero.th.utils;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.base.TestTHApp;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class NumberDisplayUtilsTest
{
    @Inject NumberDisplayUtils numberDisplayUtils;

    @Before
    public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatWithRelevantDigits_shouldRejectTooBig()
    {
        numberDisplayUtils.formatWithRelevantDigits(1000000000000000d, 1);
    }

    @Test public void formatWithRelevantDigits_zero()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(0, 10), equalTo("0"));
    }

    @Test public void formatWithRelevantDigits_10_1()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(10, 1), equalTo("10"));
    }

    @Test public void formatWithRelevantDigits_10_2()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(10, 2), equalTo("10"));
    }

    @Test public void formatWithRelevantDigits_10_3()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(10, 3), equalTo("10.0"));
    }

    @Test public void formatWithRelevantDigits_15_1()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(15, 1), equalTo("15"));
    }

    @Test public void formatWithRelevantDigits_15_2()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(15, 2), equalTo("15"));
    }

    @Test public void formatWithRelevantDigits_15_3()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(15, 3), equalTo("15.0"));
    }

    @Test public void formatWithRelevantDigits_1578_1()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(1578, 1), equalTo("2k"));
    }

    @Test public void formatWithRelevantDigits_1578_2()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(1578, 2), equalTo("1.6k"));
    }

    @Test public void formatWithRelevantDigits_1578_3()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(1578, 3), equalTo("1.58k"));
    }

    @Test public void formatWithRelevantDigits_41578_1()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(41578, 1), equalTo("42k"));
    }

    @Test public void formatWithRelevantDigits_41578_2()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(41578, 2), equalTo("42k"));
    }

    @Test public void formatWithRelevantDigits_41578_3()
    {
        assertThat(numberDisplayUtils.formatWithRelevantDigits(41578, 3), equalTo("41.6k"));
    }
}
