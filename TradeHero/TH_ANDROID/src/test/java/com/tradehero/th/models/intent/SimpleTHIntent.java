package com.tradehero.th.models.intent;

import com.tradehero.th.fragments.dashboard.RootFragmentType;

public class SimpleTHIntent extends THIntent
{
    //<editor-fold desc="Constructors">
    public SimpleTHIntent()
    {
        super();
    }
    //</editor-fold>

    @Override public RootFragmentType getDashboardType()
    {
        return RootFragmentType.TRENDING;
    }
}
