package com.tradehero.th.models.intent.portfolio;

import com.tradehero.thm.R;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;

public class PortfolioIntent extends THIntent
{
    //<editor-fold desc="Constructors">
    public PortfolioIntent()
    {
        super();
    }
    //</editor-fold>

    @Override public String getUriPath()
    {
        return getHostUriPath(R.string.intent_host_portfolio);
    }

    // TODO need to change to go to portfolio view
    @Override public DashboardTabType getDashboardType()
    {
        return DashboardTabType.TIMELINE;
    }
}
