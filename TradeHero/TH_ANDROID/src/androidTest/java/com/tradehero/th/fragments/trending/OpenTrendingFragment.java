package com.tradehero.th.fragments.trending;

import com.tradehero.th.billing.THBillingInteractorRx;

public class OpenTrendingFragment extends TrendingFragment
{
    public void set(THBillingInteractorRx userInteractorRx)
    {
        this.userInteractorRx = userInteractorRx;
    }
}
