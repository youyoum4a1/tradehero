package com.tradehero.th.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/** Created with IntelliJ IDEA. User: xavier Date: 10/17/13 Time: 4:24 PM To change this template use File | Settings | File Templates. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class MathUtilsTest
{
    @Test public void clamp_shouldReturnMin()
    {
        assertThat(MathUtils.clamp(10f, 11f, 12f), equalTo(11f));
    }

    @Test public void clamp_shouldReturnMax()
    {
        assertThat(MathUtils.clamp(13f, 11f, 12f), equalTo(12f));
    }

    @Test public void clamp_shouldReturnMinEvenSwapped()
    {
        assertThat(MathUtils.clamp(10f, 12f, 11f), equalTo(11f));
    }

    @Test public void clamp_shouldReturnMaxEvenSwapped()
    {
        assertThat(MathUtils.clamp(13f, 12f, 11f), equalTo(12f));
    }
}
