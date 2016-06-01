package com.ayondo.academy.models.number;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class THSignedMoneyTest
{
    @Test public void currencyBehaviour()
    {
        assertThat(THSignedMoney.builder(123).withOutSign().build().toString()).isEqualTo("US$ 123");
        assertThat(THSignedMoney.builder(123).withOutSign().currency(null).build().toString()).isEqualTo("US$ 123");
        assertThat(THSignedMoney.builder(123).withOutSign().currency("").build().toString()).isEqualTo("123");
        assertThat(THSignedMoney.builder(123).withOutSign().currency("SGD").build().toString()).isEqualTo("SGD 123");
    }

    @Test public void properToString()
    {
        assertThat(THSignedMoney.builder(0.8).withOutSign().currency("").build().toString()).isEqualTo("0.8");
        assertThat(THSignedMoney.builder(2.8).withOutSign().currency("SD").build().toString()).isEqualTo("SD 2.8");
        assertThat(THSignedMoney.builder(-2.8).currency("SD").build().toString()).isEqualTo("-SD 2.8");
    }
}
