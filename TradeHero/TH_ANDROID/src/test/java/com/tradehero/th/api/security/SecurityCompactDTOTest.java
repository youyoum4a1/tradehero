package com.ayondo.academy.api.security;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SecurityCompactDTOTest
{
    public static final String EXT_KEY_EXCHANGE = "exchange";

    @Test
    public void getExchangeLogoId_fromUnknownShouldReturnDefault()
    {
        SecurityCompactDTO compact = new SecurityCompactDTO();
        compact.exchange = "certainly not an exchange";
        assertThat(345).isEqualTo(compact.getExchangeLogoId(345));
    }

    @Test
    public void getExchangeLogoId_fromUnknownShouldReturn0()
    {
        SecurityCompactDTO compact = new SecurityCompactDTO();
        compact.exchange = "certainly not an exchange";
        assertThat(compact.getExchangeLogoId()).isEqualTo(0);
    }
}
