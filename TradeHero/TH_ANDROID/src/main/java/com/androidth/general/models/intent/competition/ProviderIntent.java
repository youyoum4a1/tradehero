package com.androidth.general.models.intent.competition;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.fragments.dashboard.RootFragmentType;
import com.androidth.general.models.intent.THIntent;

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
