package com.tradehero.th.models.intent;

import android.content.res.Resources;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import org.jetbrains.annotations.NotNull;

public class SimpleTHIntent extends THIntent
{
    //<editor-fold desc="Constructors">
    public SimpleTHIntent(@NotNull Resources resources)
    {
        super(resources);
    }
    //</editor-fold>

    @Override public RootFragmentType getDashboardType()
    {
        return RootFragmentType.TRENDING;
    }
}
