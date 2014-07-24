package com.tradehero.th.api.competition;

import java.util.Date;

public class ProviderDTO extends ProviderCompactDTO
{
    public Date startDateUtc;
    public Date endDateUtc;
    public String durationType;
    public String totalPrize;
    public boolean vip;

    @Override public String toString()
    {
        return "ProviderDTO{" +
                super.toString() +
                ", startDateUtc=" + startDateUtc +
                ", endDateUtc=" + endDateUtc +
                ", durationType=" + durationType +
                ", totalPrize=" + totalPrize +
                ", vip=" + vip +
        '}';
    }
}