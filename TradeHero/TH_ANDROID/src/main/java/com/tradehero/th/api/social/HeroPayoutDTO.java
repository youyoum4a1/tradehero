package com.tradehero.th.api.social;

import java.util.Date;


public class HeroPayoutDTO
{
    public static final String TAG = HeroPayoutDTO.class.getSimpleName();

    public Date payoutDateTimeUtc;
    public double usd_NetValueToHero;
    public String comments;

    public HeroPayoutDTO()
    {
        super();
    }
}
