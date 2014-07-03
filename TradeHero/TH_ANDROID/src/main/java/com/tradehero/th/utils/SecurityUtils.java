package com.tradehero.th.utils;

import com.tradehero.th.api.security.SecurityId;

public class SecurityUtils
{
    public static final String DEFAULT_VIRTUAL_CASH_CURRENCY_ISO = "USD";
    public static final String DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY = "US$";

    public static final double DEFAULT_TRANSACTION_COST = 10;
    public static final String DEFAULT_TRANSACTION_CURRENCY_ISO = "USD";
    public static final String DEFAULT_TRANSACTION_CURRENCY_DISPLAY = "US$";

    public static String getDefaultCurrency()
    {
        return DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
    }

    public static String getDisplayableSecurityName(SecurityId securityId)
    {
        return String.format("%s:%s", securityId.getExchange(), securityId.getSecuritySymbol());
    }
}
