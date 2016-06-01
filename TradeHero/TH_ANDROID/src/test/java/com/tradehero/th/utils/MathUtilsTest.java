package com.ayondo.academy.utils;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
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
