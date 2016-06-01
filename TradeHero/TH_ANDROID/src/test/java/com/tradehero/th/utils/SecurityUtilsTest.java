package com.ayondo.academy.utils;

import com.ayondo.academy.api.security.SecurityId;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class SecurityUtilsTest
{
    @Test
    public void testGetDisplayableSecurityName()
    {
        SecurityId securityId = new SecurityId("EXCHANGE", "SYMBOL");
        assertThat(SecurityUtils.getDisplayableSecurityName(securityId)).isEqualTo("EXCHANGE:SYMBOL");
    }
}