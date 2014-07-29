package com.tradehero.th.api.competition;

import java.util.Date;
import org.jetbrains.annotations.Nullable;

public class ProviderDTO extends ProviderCompactDTO
{
    public Date startDateUtc;
    public Date endDateUtc;
    public String durationType;
    public String totalPrize;
    @Nullable public Boolean vip;

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