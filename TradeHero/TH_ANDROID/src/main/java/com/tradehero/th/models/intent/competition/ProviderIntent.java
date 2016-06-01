package com.ayondo.academy.models.intent.competition;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.dashboard.RootFragmentType;
import com.ayondo.academy.models.intent.THIntent;

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
