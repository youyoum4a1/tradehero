package com.ayondo.academy.models.number;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class THSignedPercentageTest
{
    @Test public void properToString()
    {
        assertThat(THSignedPercentage.builder(0.87).withOutSign().build().toString()).isEqualTo("0.87%");
        assertThat(THSignedPercentage.builder(0.87).withOutSign().relevantDigitCount(1).build().toString()).isEqualTo("0.9%");
        assertThat(THSignedPercentage.builder(-0.87).relevantDigitCount(1).build().toString()).isEqualTo("-0.9%");
    }
}
