package com.tradehero.th.models.intent.competition;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.models.intent.THIntent;

public class ProviderIntent extends THIntent
{
    //<editor-fold desc="Constructors">
    public ProviderIntent(@NonNull Resources resources)
    {
        super(resources);
    }
    //</editor-fold>

    @Override @NonNull public String getUriPath()
    {
        return getHostUriPath(resources, R.string.intent_host_providers);
    }

    @Override public RootFragmentType getDashboardType()
    {
        return RootFragmentType.COMMUNITY;
    }
}
