package com.tradehero.th.api.social;

import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 9:24 PM To change this template use File | Settings | File Templates. */
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
