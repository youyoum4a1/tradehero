package com.tradehero.th.models.intent;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.fragments.dashboard.RootFragmentType;

public class SimpleTHIntent extends THIntent
{
    //<editor-fold desc="Constructors">
    public SimpleTHIntent(@NonNull Resources resources)
    {
        super(resources);
    }
    //</editor-fold>

    @Override public RootFragmentType getDashboardType()
    {
        return RootFragmentType.TRENDING;
    }
}
