package com.tradehero.th.api.market;

import com.tradehero.th.R;

public enum Exchange
{
    // United Kingdom
    LSE(R.drawable.square_gb/*R.drawable.flag_country_round_united_kingdom*/),

    // United States of America
    NASDAQ(R.drawable.square_us/*R.drawable.flag_country_round_united_states*/),
    NYSE(R.drawable.square_us/*R.drawable.flag_country_round_united_states*/),
    OTCBB(R.drawable.square_us/*R.drawable.flag_country_round_united_states*/),
    AMEX(R.drawable.square_us/*R.drawable.flag_country_round_united_states*/),

    // Singapore
    SGX(R.drawable.square_sg/*R.drawable.flag_country_round_singapore*/),

    // Australia
    ASX(R.drawable.square_au/*R.drawable.flag_country_round_australia*/),

    // Canada
    TSX(R.drawable.square_ca/*R.drawable.flag_country_round_canada*/),
    TSXV(R.drawable.square_ca/*R.drawable.flag_country_round_canada*/),

    // Hong Kong
    HKEX(R.drawable.square_hk/*R.drawable.flag_country_round_hong_kong*/),

    // France
    PAR(R.drawable.square_fr/*R.drawable.flag_country_round_france*/),

    // Netherlands
    AMS(R.drawable.square_nl/*R.drawable.flag_country_round_netherlands*/),

    // Belgium
    BRU(R.drawable.square_br/*R.drawable.flag_country_round_belgium*/),

    // Portugal
    LIS(R.drawable.square_pt/*R.drawable.flag_country_round_portugal*/),

    // Italy
    MLSE(R.drawable.square_it/*R.drawable.flag_country_round_italy*/),

    // New Zealand
    NZX(R.drawable.square_nz/*R.drawable.flag_country_round_new_zealand*/),

    // China
    SHA(R.drawable.square_cn/*R.drawable.flag_country_round_china*/),
    SHE(R.drawable.square_cn/*R.drawable.flag_country_round_china*/),

    // Indonesia
    JKT(R.drawable.square_id/*R.drawable.flag_country_round_indonesia*/),

    // South Korea
    KDQ(R.drawable.square_kr/*R.drawable.flag_country_round_korea_south*/),
    KRX(R.drawable.square_kr/*R.drawable.flag_country_round_korea_south*/),

    // Taiwan
    TPE(R.drawable.square_tw/*R.drawable.flag_country_round_taiwan*/),

    // Thailand
    SET(R.drawable.square_th/*R.drawable.flag_country_round_thailand*/),

    // Philippines
    PSE(R.drawable.square_ph/*R.drawable.flag_country_round_philippines*/),

    // Malaysia
    MYX(R.drawable.square_my/*R.drawable.flag_country_round_malaysia*/),

    // India
    NSE(R.drawable.square_in/*R.drawable.flag_country_round_india*/),
    BSE(R.drawable.square_in/*R.drawable.flag_country_round_india*/),

    // Japan
    TSE(R.drawable.square_jp/*R.drawable.flag_country_round_japan*/);

    public final int logoId;

    private Exchange(int logoId)
    {
        this.logoId = logoId;
    }
}
