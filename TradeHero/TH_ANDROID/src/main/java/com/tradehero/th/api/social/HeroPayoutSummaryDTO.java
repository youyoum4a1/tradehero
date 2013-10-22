package com.tradehero.th.api.social;

import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 9:23 PM To change this template use File | Settings | File Templates. */
public class HeroPayoutSummaryDTO
{
    public static final String TAG = HeroPayoutSummaryDTO.class.getSimpleName();

    public List<HeroPayoutDTO> payouts;
    public double totalPayout;

    public HeroPayoutSummaryDTO()
    {
        super();
    }
}
