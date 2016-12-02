package com.androidth.general.utils;

import android.support.annotation.NonNull;
import com.androidth.general.api.security.SecurityId;

public class SecurityUtils
{
    public static final String DEFAULT_VIRTUAL_CASH_CURRENCY_ISO = "USD";
    public static final String DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY = "US$";

    public static final String DEFAULT_VIRTUAL_CASH_BONUS_CURRENCY_DISPLAY = "TH$";

    public static final double DEFAULT_TRANSACTION_COST_USD = 0;
    public static final String DEFAULT_TRANSACTION_CURRENCY_ISO = "USD";
    public static final String DEFAULT_TRANSACTION_CURRENCY_DISPLAY = "US$";

    public static final String FX_EXCHANGE = "FX";

    @NonNull public static String getDefaultCurrency()
    {
        return DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
    }

    @NonNull public static String getDisplayableSecurityName(SecurityId securityId)
    {
        return String.format("%s:%s", securityId.getExchange(), securityId.getSecuritySymbol());
    }

    public static boolean isFX(@NonNull SecurityId securityId)
    {
        return securityId.getExchange().equals(FX_EXCHANGE);
    }
}
