package com.tradehero.th.api.market;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 9/16/13 Time: 11:52 AM To change this template use File | Settings | File Templates. */
public enum Exchange
{
    // United Kingdom
    LSE(R.drawable.united_kingdom),

    // United States of America
    NASDAQ(R.drawable.united_states),
    NYSE(R.drawable.united_states),
    OTCBB(R.drawable.united_states),
    AMEX(R.drawable.united_states),

    // singapore
    SGX(R.drawable.singapore),

    // Australia
    ASX(R.drawable.australia),

    // Canada
    TSX(R.drawable.canada),
    TSXV(R.drawable.canada),

    // Hong Kong
    HKEX(R.drawable.hong_kong),

    // france
    PAR(R.drawable.france),

    // netherlands
    AMS(R.drawable.netherlands),

    // Belgium
    BRU(R.drawable.belgium),

    // portugal
    LIS(R.drawable.portugal),

    // italy
    MLSE(R.drawable.italy),

    // New Zealand
    NZX(R.drawable.new_zealand),

    // china
    SHA(R.drawable.china),
    SHE(R.drawable.china),

    // indonesia
    JKT(R.drawable.indonesia),

    // South Korea
    KDQ(R.drawable.korea_south),
    KRX(R.drawable.korea_south),

    // taiwan
    TPE(R.drawable.taiwan),

    // thailand
    SET(R.drawable.thailand),

    // philippines
    PSE(R.drawable.philippines),

    // malaysia
    MYX(R.drawable.malaysia);

    private static final String TAG = Exchange.class.getSimpleName();

    public final int logoId;

    private Exchange(int logoId)
    {
        this.logoId = logoId;
    }
}
