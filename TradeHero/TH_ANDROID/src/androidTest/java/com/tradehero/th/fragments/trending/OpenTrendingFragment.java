package com.tradehero.th.fragments.trending;

import com.tradehero.th.billing.THBillingInteractor;

public class OpenTrendingFragment extends TrendingStockFragment
{
    public void set(THBillingInteractor userInteractor)
    {
        this.userInteractor = userInteractor;
    }
}
