package com.tradehero.th.models.intent.competition;

import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;

/**
 * Created by xavier on 1/10/14.
 */
public class ProviderIntent extends THIntent
{
    public static final String TAG = ProviderIntent.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public ProviderIntent()
    {
        super();
    }
    //</editor-fold>

    @Override public String getUriPath()
    {
        return getHostUriPath(R.string.intent_host_providers);
    }

    @Override public DashboardTabType getDashboardType()
    {
        return DashboardTabType.COMMUNITY;
    }
}
