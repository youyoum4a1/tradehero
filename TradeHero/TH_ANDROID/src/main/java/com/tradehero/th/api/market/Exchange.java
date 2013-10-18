package com.tradehero.th.api.market;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 9/16/13 Time: 11:52 AM To change this template use File | Settings | File Templates. */
public enum Exchange
{
    // United Kingdom
    @ExchangeInfo(logoId = R.drawable.united_kingdom)
    LSE,

    // United States of America
    @ExchangeInfo(logoId = R.drawable.united_states)
    NASDAQ,
    @ExchangeInfo(logoId = R.drawable.united_states)
    NYSE,
    @ExchangeInfo(logoId = R.drawable.united_states)
    OTCBB,
    @ExchangeInfo(logoId = R.drawable.united_states)
    AMEX,

    // singapore
    @ExchangeInfo(logoId = R.drawable.singapore)
    SGX,

    // Australia
    @ExchangeInfo(logoId = R.drawable.australia)
    ASX,

    // Canada
    @ExchangeInfo(logoId = R.drawable.canada)
    TSX,
    @ExchangeInfo(logoId = R.drawable.canada)
    TSXV,

    // Hong Kong
    @ExchangeInfo(logoId = R.drawable.hong_kong)
    HKEX,

    // france
    @ExchangeInfo(logoId = R.drawable.france)
    PAR,

    // netherlands
    @ExchangeInfo(logoId = R.drawable.netherlands)
    AMS,

    // Belgium
    @ExchangeInfo(logoId = R.drawable.belgium)
    BRU,

    // portugal
    @ExchangeInfo(logoId = R.drawable.portugal)
    LIS,

    // italy
    @ExchangeInfo(logoId = R.drawable.italy)
    MLSE,

    // New Zealand
    @ExchangeInfo(logoId = R.drawable.new_zealand)
    NZX,

    // china
    @ExchangeInfo(logoId = R.drawable.china)
    SHA,
    @ExchangeInfo(logoId = R.drawable.china)
    SHE,

    // indonesia
    @ExchangeInfo(logoId = R.drawable.indonesia)
    JKT,

    // South Korea
    @ExchangeInfo(logoId = R.drawable.korea_south)
    KDQ,
    @ExchangeInfo(logoId = R.drawable.korea_south)
    KRX,

    // taiwan
    @ExchangeInfo(logoId = R.drawable.taiwan)
    TPE,

    // thailand
    @ExchangeInfo(logoId = R.drawable.thailand)
    SET,

    // philippines
    @ExchangeInfo(logoId = R.drawable.philippines)
    PSE,

    // malaysia
    @ExchangeInfo(logoId = R.drawable.malaysia)
    MYX;

    private static final String TAG = Exchange.class.getSimpleName();

    public static int getLogoId(Exchange exchange)
    {
        return getLogoId(exchange.name());
    }

    public static int getLogoId(String exchange)
    {
        try
        {
            return Exchange.class.getField(exchange).getAnnotation(ExchangeInfo.class).logoId();
        }
        catch (NoSuchFieldException e)
        {
            THLog.e(TAG, "Unavailable exchange name " + exchange, e);
            return 0;
        }
    }
}
