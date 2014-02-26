package com.tradehero.th.api.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/** Created with IntelliJ IDEA. User: tho Date: 9/20/13 Time: 6:07 PM Copyright (c) TradeHero */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SecurityCompactDTOTest
{
    public static final String EXT_KEY_EXCHANGE = "exchange";

    @Test(expected = NullPointerException.class)
    public void getExchangeLogoId_fromNullShouldThrowNull()
    {
        new SecurityCompactDTO().getExchangeLogoId();
    }

    @Test
    public void getExchangeLogoId_fromUnknownShouldReturnDefault()
    {
        SecurityCompactDTO compact = new SecurityCompactDTO();
        compact.exchange = "certainly not an exchange";
        assertEquals(345, compact.getExchangeLogoId(345));
    }

    @Test
    public void getExchangeLogoId_fromUnknownShouldReturn0()
    {
        SecurityCompactDTO compact = new SecurityCompactDTO();
        compact.exchange = "certainly not an exchange";
        assertEquals(0, compact.getExchangeLogoId());
    }

    @Test()
    public void getSecurityType_fromUnknownIsNull()
    {
        SecurityCompactDTO compact = new SecurityCompactDTO();
        compact.securityType = 999959;
        assertNull(compact.getSecurityType());
    }
}
