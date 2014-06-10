package com.tradehero.th.models.intent;

import com.tradehero.th.fragments.dashboard.DashboardTabType;

public class SimpleTHIntent extends THIntent
{
    //<editor-fold desc="Constructors">
    public SimpleTHIntent()
    {
        super();
    }
    //</editor-fold>

    @Override public DashboardTabType getDashboardType()
    {
        return DashboardTabType.TRENDING;
    }
}
