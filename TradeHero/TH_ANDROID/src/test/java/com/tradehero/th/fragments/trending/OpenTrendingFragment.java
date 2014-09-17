package com.tradehero.th.fragments.trending;

import com.tradehero.th.billing.THBillingInteractor;

public class OpenTrendingFragment extends TrendingFragment
{
    public void set(THBillingInteractor userInteractor)
    {
        this.userInteractor = userInteractor;
    }
}
