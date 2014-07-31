package com.tradehero.th.models.number;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class THSignedNumberTest
{
    @Test(expected = IllegalArgumentException.class)
    public void cannotHandleNullNumber()
    {
        THSignedNumber.builder().build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotHandleNonMoneyWithCurrency()
    {
        THSignedNumber.builder().number(123).currency("").build();
    }

    @Test public void precisionAsExpected()
    {
        assertThat(THSignedNumber.builder().number(10000).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(4130).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(1000).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(999).build().getPrecisionFromNumber()).isEqualTo(1);
        assertThat(THSignedNumber.builder().number(100).build().getPrecisionFromNumber()).isEqualTo(1);
        assertThat(THSignedNumber.builder().number(99).build().getPrecisionFromNumber()).isEqualTo(2);
        assertThat(THSignedNumber.builder().number(10).build().getPrecisionFromNumber()).isEqualTo(2);
        assertThat(THSignedNumber.builder().number(9).build().getPrecisionFromNumber()).isEqualTo(3);
        assertThat(THSignedNumber.builder().number(1).build().getPrecisionFromNumber()).isEqualTo(3);
        assertThat(THSignedNumber.builder().number(0.999999).build().getPrecisionFromNumber()).isEqualTo(4);
        assertThat(THSignedNumber.builder().number(0.1).build().getPrecisionFromNumber()).isEqualTo(4);
        assertThat(THSignedNumber.builder().number(0.0999999).build().getPrecisionFromNumber()).isEqualTo(5);
        assertThat(THSignedNumber.builder().number(0.01).build().getPrecisionFromNumber()).isEqualTo(5);
        assertThat(THSignedNumber.builder().number(0.0099999).build().getPrecisionFromNumber()).isEqualTo(6);
        assertThat(THSignedNumber.builder().number(0.001).build().getPrecisionFromNumber()).isEqualTo(6);
    }

    @Test public void precisionAsExpected3()
    {
        assertThat(THSignedNumber.builder().number(10000).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(4130).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(1000).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(999).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(100).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(0);
        assertThat(THSignedNumber.builder().number(99).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(1);
        assertThat(THSignedNumber.builder().number(10).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(1);
        assertThat(THSignedNumber.builder().number(9).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(2);
        assertThat(THSignedNumber.builder().number(1).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(2);
        assertThat(THSignedNumber.builder().number(0.999999).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(3);
        assertThat(THSignedNumber.builder().number(0.1).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(3);
        assertThat(THSignedNumber.builder().number(0.0999999).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(4);
        assertThat(THSignedNumber.builder().number(0.01).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(4);
        assertThat(THSignedNumber.builder().number(0.0099999).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(5);
        assertThat(THSignedNumber.builder().number(0.001).relevantDigitCount(3).build().getPrecisionFromNumber()).isEqualTo(5);
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
        assertThat(THSignedNumber.builder().number(123).withSign().signTypeArrow().build().getSignPrefix()).isEqualTo("▲");
        assertThat(THSignedNumber.builder().number(-123).withSign().signTypeArrow().build().getSignPrefix()).isEqualTo("▼");
        assertThat(THSignedNumber.builder().number(123).withSign().signTypeMinusOnly().build().getSignPrefix()).isEqualTo("");
        assertThat(THSignedNumber.builder().number(-123).withSign().signTypeMinusOnly().build().getSignPrefix()).isEqualTo("-");
        assertThat(THSignedNumber.builder().number(123).withSign().signTypePlusMinusAlways().build().getSignPrefix()).isEqualTo("+");
        assertThat(THSignedNumber.builder().number(-123).withSign().signTypePlusMinusAlways().build().getSignPrefix()).isEqualTo("-");
    }

    @Test public void currencyBehaviour()
    {
        assertThat(THSignedNumber.builder().number(123).withOutSign().money().currency(null).build().toString()).isEqualTo("US$ 123");
        assertThat(THSignedNumber.builder().number(123).withOutSign().money().currency("").build().toString()).isEqualTo("123");
        assertThat(THSignedNumber.builder().number(123).withOutSign().money().currency("SGD").build().toString()).isEqualTo("SGD 123");
        assertThat(THSignedNumber.builder().number(123).withOutSign().build().toString()).isEqualTo("123");
    }

    @Test public void properToString()
    {
        assertThat(THSignedNumber.builder().number(0.87).percentage().withOutSign().build().toString()).isEqualTo("0.87%");
        assertThat(THSignedNumber.builder().number(0.8).money().withOutSign().currency("").build().toString()).isEqualTo("0.8");
        assertThat(THSignedNumber.builder().number(2.8).money().withOutSign().currency("SD").build().toString()).isEqualTo("SD 2.8");
    }
}
