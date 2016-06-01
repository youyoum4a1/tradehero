package com.ayondo.academy.fragments.trending;

import com.ayondo.academy.billing.THBillingInteractorRx;

public class OpenTrendingFragment extends TrendingStockFragment
{
    public void set(THBillingInteractorRx userInteractorRx)
    {
        this.userInteractorRx = userInteractorRx;
    }
}
