package com.tradehero.th.api.social;

import java.util.List;


public class HeroPayoutSummaryDTO
{
    public static final String TAG = HeroPayoutSummaryDTO.class.getSimpleName();
    //Histroy that TH gives money to hero
    public List<HeroPayoutDTO> payouts;
    public double totalPayout;

    public HeroPayoutSummaryDTO()
    {
        super();
    }
}
