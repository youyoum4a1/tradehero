package com.tradehero.th.utils;

import android.text.TextUtils;

import com.tradehero.th.api.security.SecurityId;

public class SecurityUtils {
    public static final String DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY = "$";

    public static final double DEFAULT_TRANSACTION_COST_USD = 0;//现在没有手续费了
    public static final String DEFAULT_TRANSACTION_CURRENCY_ISO = "USD";

    public static String getDefaultCurrency()
    {
        return DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
    }

    public static String getDisplayableSecurityName(SecurityId securityId)
    {
        return String.format("%s:%s", securityId.getExchange(), securityId.getSecuritySymbol());
    }

    public static String getCurrencyShortDispaly(String display)
    {
        if (StringUtils.isNullOrEmpty(display))
        {
            return "";
        }
        else
        {
            if (display.startsWith("CN"))
            {
                return display.replace("CN", "");
            }
            if (display.startsWith("US"))
            {
                return display.replace("US", "");
            }
        }
        return display;
    }

    public static String getMarketCodeBySymbol(String symbol){
        if(TextUtils.isEmpty(symbol)){
            return "-1";
        }
        if(symbol.startsWith("420")){
            return "6";
        }
        if(symbol.startsWith("400")){
            return "5";
        }
        if(symbol.startsWith("9")){
            return "2";
        }
        if(symbol.startsWith("2")){
            return "3";
        }
        if(symbol.startsWith("6")){
            return "0";
        }
        if(symbol.startsWith("0")){
            return "1";
        }
        return "7";
    }
}
