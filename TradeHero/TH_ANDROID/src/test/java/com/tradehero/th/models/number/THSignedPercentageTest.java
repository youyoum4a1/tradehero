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
        assertThat(THSignedPercentage.builder().value(0.87).withOutSign().build().toString()).isEqualTo("0.87%");
    }
}
