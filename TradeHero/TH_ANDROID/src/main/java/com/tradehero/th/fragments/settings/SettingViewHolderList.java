package com.tradehero.th.fragments.settings;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SettingViewHolderList extends ArrayList<SettingViewHolder>
{
    //<editor-fold desc="Constructors">
    public SettingViewHolderList()
    {
        super();
    }
    //</editor-fold>

    public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        for (@NotNull SettingViewHolder viewHolder : this)
        {
            viewHolder.initViews(preferenceFragment);
        }
    }

    public void destroyViews()
    {
        for (@NotNull SettingViewHolder viewHolder : this)
        {
            viewHolder.destroyViews();
        }
    }

    @Nullable public SettingViewHolder getFirstUnread()
    {
        for (@NotNull SettingViewHolder viewHolder : this)
        {
            if (viewHolder.isUnread())
            {
                return viewHolder;
            }
        }
        return null;
    }
}
