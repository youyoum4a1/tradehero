package com.tradehero.th.models.intent.trending;

import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;

public class TrendingIntent extends THIntent
{
    //<editor-fold desc="Constructors">
    public TrendingIntent()
    {
        super();
        setData(getUri());
    }
    //</editor-fold>

    @Override public String getUriPath()
    {
        return getHostUriPath(R.string.intent_host_trending);
    }

    @Override public DashboardTabType getDashboardType()
    {
        return DashboardTabType.TRENDING;
    }
}
