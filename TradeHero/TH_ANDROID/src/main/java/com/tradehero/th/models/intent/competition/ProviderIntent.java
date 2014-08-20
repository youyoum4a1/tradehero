package com.tradehero.th.models.intent.competition;

import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.models.intent.THIntent;

public class ProviderIntent extends THIntent
{
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

    @Override public RootFragmentType getDashboardType()
    {
        return RootFragmentType.COMMUNITY;
    }
}
