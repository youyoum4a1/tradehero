package com.ayondo.academy.models.number;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class THSignedNumberTest
{
    @Test public void precisionAsExpectedDefault()
    {
        assertThat(THSignedNumber.builder(10000).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder(4130).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder(1000).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder(999).build().getPrecisionFromNumber()).isEqualTo(1);
        assertThat(THSignedNumber.builder(100).build().getPrecisionFromNumber()).isEqualTo(1);
        assertThat(THSignedNumber.builder(99).build().getPrecisionFromNumber()).isEqualTo(2);
        assertThat(THSignedNumber.builder(10).build().getPrecisionFromNumber()).isEqualTo(2);
        assertThat(THSignedNumber.builder(9).build().getPrecisionFromNumber()).isEqualTo(3);
        assertThat(THSignedNumber.builder(1).build().getPrecisionFromNumber()).isEqualTo(3);
        assertThat(THSignedNumber.builder(0.999999).build().getPrecisionFromNumber()).isEqualTo(4);
        assertThat(THSignedNumber.builder(0.1).build().getPrecisionFromNumber()).isEqualTo(4);
        assertThat(THSignedNumber.builder(0.0999999).build().getPrecisionFromNumber()).isEqualTo(5);
        assertThat(THSignedNumber.builder(0.01).build().getPrecisionFromNumber()).isEqualTo(5);
        assertThat(THSignedNumber.builder(0.0099999).build().getPrecisionFromNumber()).isEqualTo(6);
        assertThat(THSignedNumber.builder(0.001).build().getPrecisionFromNumber()).isEqualTo(6);
    }

    @Test public void precisionAsExpected3()
    {
        assertThat(THSignedNumber.builder(10000).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder(4130).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder(1000).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder(999).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder(100).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder(99).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(1);
        assertThat(THSignedNumber.builder(10).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(1);
        assertThat(THSignedNumber.builder(9).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(2);
        assertThat(THSignedNumber.builder(1).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(2);
        assertThat(THSignedNumber.builder(0.999999).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(3);
        assertThat(THSignedNumber.builder(0.1).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(3);
        assertThat(THSignedNumber.builder(0.0999999).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(4);
        assertThat(THSignedNumber.builder(0.01).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(4);
        assertThat(THSignedNumber.builder(0.0099999).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(5);
        assertThat(THSignedNumber.builder(0.001).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(5);
    }

    @Test public void stringFormatAsExpected()
    {
        assertThat(THSignedNumber.getStringFormat(-1).toString()).isEqualTo("#,###");
        assertThat(THSignedNumber.getStringFormat(0).toString()).isEqualTo("#,###");
        assertThat(THSignedNumber.getStringFormat(1).toString()).isEqualTo("#,###.#");
        assertThat(THSignedNumber.getStringFormat(2).toString()).isEqualTo("#,###.##");
        assertThat(THSignedNumber.getStringFormat(3).toString()).isEqualTo("#,###.###");
        assertThat(THSignedNumber.getStringFormat(4).toString()).isEqualTo("#,###.####");
    }

    @Test public void removeTrailingZeroes()
    {
        assertThat(THSignedNumber.removeTrailingZeros("123")).isEqualTo("123");
        assertThat(THSignedNumber.removeTrailingZeros("123.")).isEqualTo("123");
        assertThat(THSignedNumber.removeTrailingZeros("123.0")).isEqualTo("123");
        assertThat(THSignedNumber.removeTrailingZeros("123.00")).isEqualTo("123");
        assertThat(THSignedNumber.removeTrailingZeros("123.01")).isEqualTo("123.01");
        assertThat(THSignedNumber.removeTrailingZeros("123.11")).isEqualTo("123.11");
        assertThat(THSignedNumber.removeTrailingZeros("123.10")).isEqualTo("123.1");
        assertThat(THSignedNumber.removeTrailingZeros("123.100")).isEqualTo("123.1");
    }

    @Test public void properPrefix()
    {
        assertThat(THSignedNumber.builder(123).withSign().signTypeArrow().build().getSignPrefix()).isEqualTo("▲");
        assertThat(THSignedNumber.builder(-123).withSign().signTypeArrow().build().getSignPrefix()).isEqualTo("▼");
        assertThat(THSignedNumber.builder(123).withSign().signTypeMinusOnly().build().getSignPrefix()).isEqualTo("");
        assertThat(THSignedNumber.builder(-123).withSign().signTypeMinusOnly().build().getSignPrefix()).isEqualTo("-");
        assertThat(THSignedNumber.builder(123).withSign().signTypePlusMinusAlways().build().getSignPrefix()).isEqualTo("+");
        assertThat(THSignedNumber.builder(-123).withSign().signTypePlusMinusAlways().build().getSignPrefix()).isEqualTo("-");
    }

    @Test public void properRelevantDigitCount()
    {
        assertThat(THSignedNumber.builder(2.812546).withOutSign().relevantDigitCount(-1).build().toString()).isEqualTo("3");
        assertThat(THSignedNumber.builder(2.812546).withOutSign().relevantDigitCount(0).build().toString()).isEqualTo("3");
        assertThat(THSignedNumber.builder(2.812546).withOutSign().relevantDigitCount(1).build().toString()).isEqualTo("3");
        assertThat(THSignedNumber.builder(2.812546).withOutSign().relevantDigitCount(2).build().toString()).isEqualTo("2.8");
        assertThat(THSignedNumber.builder(2.812546).withOutSign().relevantDigitCount(3).build().toString()).isEqualTo("2.81");
        assertThat(THSignedNumber.builder(2.812546).withOutSign().relevantDigitCount(4).build().toString()).isEqualTo("2.813");
        assertThat(THSignedNumber.builder(2.812546).withOutSign().relevantDigitCount(5).build().toString()).isEqualTo("2.8125");
        assertThat(THSignedNumber.builder(2.812546).withOutSign().relevantDigitCount(6).build().toString()).isEqualTo("2.81255");
        assertThat(THSignedNumber.builder(2.812546).withOutSign().relevantDigitCount(7).build().toString()).isEqualTo("2.812546");
        assertThat(THSignedNumber.builder(2.812546).withOutSign().relevantDigitCount(8).build().toString()).isEqualTo("2.812546");
    }

    @Test public void properToString()
    {
        assertThat(THSignedNumber.builder(0.8).withOutSign().build().toString()).isEqualTo("0.8");
    }
}
