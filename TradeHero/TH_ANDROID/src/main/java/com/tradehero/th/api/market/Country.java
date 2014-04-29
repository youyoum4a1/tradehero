package com.tradehero.th.api.market;

import com.tradehero.th.R;

public enum Country
{
    // United Kingdom
    UK(R.drawable.flag_country_round_united_kingdom),

    // United States of America
    US(R.drawable.flag_country_round_united_states),

    // Singapore
    SG(R.drawable.flag_country_round_singapore),

    // Australia
    AU(R.drawable.flag_country_round_australia),

    // Canada
    CA(R.drawable.flag_country_round_canada),

    // Hong Kong
    HK(R.drawable.flag_country_round_hong_kong),

    // France
    FR(R.drawable.flag_country_round_france),

    // Netherlands
    NL(R.drawable.flag_country_round_netherlands),

    // Belgium
    BE(R.drawable.flag_country_round_belgium),

    // Portugal
    PT(R.drawable.flag_country_round_portugal),

    // Italy
    IT(R.drawable.flag_country_round_italy),

    // New Zealand
    NZ(R.drawable.flag_country_round_new_zealand),

    // China
    CN(R.drawable.flag_country_round_china),

    // Indonesia
    ID(R.drawable.flag_country_round_indonesia),

    // South Korea
    KR(R.drawable.flag_country_round_korea_south),

    // Taiwan
    TW(R.drawable.flag_country_round_taiwan),

    // Thailand
    TH(R.drawable.flag_country_round_thailand),

    // Philippines
    PH(R.drawable.flag_country_round_philippines),

    // Malaysia
    MY(R.drawable.flag_country_round_malaysia),

    // India
    IN(R.drawable.flag_country_round_india),

    // Japan
    JP(R.drawable.flag_country_round_japan);

    public final int logoId;

    private Country(int logoId)
    {
        this.logoId = logoId;
    }
}
