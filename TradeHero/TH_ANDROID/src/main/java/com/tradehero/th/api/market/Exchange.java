package com.tradehero.th.api.market;

import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 9/16/13 Time: 11:52 AM To change this template use File | Settings | File Templates. */
public enum Exchange
{
    // United Kingdom
    LSE(R.drawable.flag_country_round_united_kingdom),

    // United States of America
    NASDAQ(R.drawable.flag_country_round_united_states),
    NYSE(R.drawable.flag_country_round_united_states),
    OTCBB(R.drawable.flag_country_round_united_states),
    AMEX(R.drawable.flag_country_round_united_states),

    // Singapore
    SGX(R.drawable.flag_country_round_singapore),

    // Australia
    ASX(R.drawable.flag_country_round_australia),

    // Canada
    TSX(R.drawable.flag_country_round_canada),
    TSXV(R.drawable.flag_country_round_canada),

    // Hong Kong
    HKEX(R.drawable.flag_country_round_hong_kong),

    // France
    PAR(R.drawable.flag_country_round_france),

    // Netherlands
    AMS(R.drawable.flag_country_round_netherlands),

    // Belgium
    BRU(R.drawable.flag_country_round_belgium),

    // Portugal
    LIS(R.drawable.flag_country_round_portugal),

    // Italy
    MLSE(R.drawable.flag_country_round_italy),

    // New Zealand
    NZX(R.drawable.flag_country_round_new_zealand),

    // China
    SHA(R.drawable.flag_country_round_china),
    SHE(R.drawable.flag_country_round_china),

    // Indonesia
    JKT(R.drawable.flag_country_round_indonesia),

    // South Korea
    KDQ(R.drawable.flag_country_round_korea_south),
    KRX(R.drawable.flag_country_round_korea_south),

    // Taiwan
    TPE(R.drawable.flag_country_round_taiwan),

    // Thailand
    SET(R.drawable.flag_country_round_thailand),

    // Philippines
    PSE(R.drawable.flag_country_round_philippines),

    // Malaysia
    MYX(R.drawable.flag_country_round_malaysia),

    // India
    NSE(R.drawable.flag_country_round_india),
    BSE(R.drawable.flag_country_round_india),

    // Japan
    TSE(R.drawable.flag_country_round_japan);

    public final int logoId;

    private Exchange(int logoId)
    {
        this.logoId = logoId;
    }
}
