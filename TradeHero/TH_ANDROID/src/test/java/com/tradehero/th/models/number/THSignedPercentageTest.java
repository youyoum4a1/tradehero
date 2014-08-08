package com.tradehero.th.models.number;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class THSignedPercentageTest
{
    @Test public void properToString()
    {
        assertThat(THSignedPercentage.builder(0.87).withOutSign().build().toString()).isEqualTo("0.87%");
        assertThat(THSignedPercentage.builder(0.87).withOutSign().relevantDigitCount(1).build().toString()).isEqualTo("0.9%");
        assertThat(THSignedPercentage.builder(-0.87).relevantDigitCount(1).build().toString()).isEqualTo("-0.9%");
    }
}
