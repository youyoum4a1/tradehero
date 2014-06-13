package com.tradehero.th.api.security;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
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

    @Test
    public void getSecurityType_fromUnknownIsNull()
    {
        SecurityCompactDTO compact = new SecurityCompactDTO();
        compact.securityType = 999959;
        assertThat(compact.getSecurityType()).isNull();
    }
}
