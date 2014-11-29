package com.tradehero.th.fragments.settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;

public class SettingViewHolderList extends ArrayList<SettingViewHolder>
{
    //<editor-fold desc="Constructors">
    public SettingViewHolderList()
    {
        super();
    }
    //</editor-fold>

    public void initViews(@NonNull DashboardPreferenceFragment preferenceFragment)
    {
        for (SettingViewHolder viewHolder : this)
        {
            viewHolder.initViews(preferenceFragment);
        }
    }

    public void destroyViews()
    {
        for (SettingViewHolder viewHolder : this)
        {
            viewHolder.destroyViews();
        }
    }

    @Nullable public SettingViewHolder getFirstUnread()
    {
        for (SettingViewHolder viewHolder : this)
        {
            if (viewHolder.isUnread())
            {
                return viewHolder;
            }
        }
        return null;
    }
}
