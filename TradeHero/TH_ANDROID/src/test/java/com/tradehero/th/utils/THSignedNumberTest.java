package com.tradehero.th.utils;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class THSignedNumberTest
{
    @Test public void precisionAsExpected()
    {
        assertThat(THSignedNumber.builder().number(10000).build().precisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(4130).build().precisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(1000).build().precisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(999).build().precisionFromNumber()).isEqualTo(1);
        assertThat(THSignedNumber.builder().number(100).build().precisionFromNumber()).isEqualTo(1);
        assertThat(THSignedNumber.builder().number(99).build().precisionFromNumber()).isEqualTo(2);
        assertThat(THSignedNumber.builder().number(10).build().precisionFromNumber()).isEqualTo(2);
        assertThat(THSignedNumber.builder().number(9).build().precisionFromNumber()).isEqualTo(3);
        assertThat(THSignedNumber.builder().number(1).build().precisionFromNumber()).isEqualTo(3);
        assertThat(THSignedNumber.builder().number(0.999999).build().precisionFromNumber()).isEqualTo(4);
        assertThat(THSignedNumber.builder().number(0.1).build().precisionFromNumber()).isEqualTo(4);
        assertThat(THSignedNumber.builder().number(0.0999999).build().precisionFromNumber()).isEqualTo(5);
        assertThat(THSignedNumber.builder().number(0.01).build().precisionFromNumber()).isEqualTo(5);
        assertThat(THSignedNumber.builder().number(0.0099999).build().precisionFromNumber()).isEqualTo(6);
        assertThat(THSignedNumber.builder().number(0.001).build().precisionFromNumber()).isEqualTo(6);
    }
}
