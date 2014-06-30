package com.tradehero.th.api.market;

import com.tradehero.thm.R;

public enum Exchange
{
    // United Kingdom
    LSE(R.drawable.square_gb, true),

    // United States of America
    NASDAQ(R.drawable.square_us, false),
    NYSE(R.drawable.square_us, true),
    OTCBB(R.drawable.square_us, false),
    AMEX(R.drawable.square_us, false),

    // Singapore
    SGX(R.drawable.square_sg, true),

    // Australia
    ASX(R.drawable.square_au, true),

    // Canada
    TSX(R.drawable.square_ca, true),
    TSXV(R.drawable.square_ca, false),

    // Hong Kong
    HKEX(R.drawable.square_hk, true),

    // France
    PAR(R.drawable.square_fr, true),

    // Netherlands
    AMS(R.drawable.square_nl, true),

    // Belgium
    BRU(R.drawable.square_be, true),

    // Portugal
    LIS(R.drawable.square_pt, true),

    // Italy
    MLSE(R.drawable.square_it, true),

    // New Zealand
    NZX(R.drawable.square_nz, true),

    // China
    SHA(R.drawable.square_cn, true),
    SHE(R.drawable.square_cn, false),

    // Indonesia
    JKT(R.drawable.square_id, true),

    // South Korea
    KDQ(R.drawable.square_kr, true),
    KRX(R.drawable.square_kr, false),

    // Taiwan
    TPE(R.drawable.square_tw, true),

    // Thailand
    SET(R.drawable.square_th, true),

    // Philippines
    PSE(R.drawable.square_ph, true),

    // Malaysia
    MYX(R.drawable.square_my, true),

    // India
    NSE(R.drawable.square_in, false),
    BSE(R.drawable.square_in, true),

    // Japan
    TSE(R.drawable.square_jp, true);

    public final int logoId;
    public final boolean isCountryDefault;

    private Exchange(int logoId, boolean isCountryDefault)
    {
        this.logoId = logoId;
        this.isCountryDefault = isCountryDefault;
    }
}
