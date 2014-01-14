package com.tradehero.th.models.intent;

import com.tradehero.th.fragments.dashboard.DashboardTabType;

/**
 * Created by xavier on 1/13/14.
 */
public class SimpleTHIntent extends THIntent
{
    public static final String TAG = SimpleTHIntent.class.getSimpleName();

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
