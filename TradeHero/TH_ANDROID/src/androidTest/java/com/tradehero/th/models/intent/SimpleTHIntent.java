package com.tradehero.th.models.intent;

import android.content.res.Resources;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import android.support.annotation.NonNull;

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
