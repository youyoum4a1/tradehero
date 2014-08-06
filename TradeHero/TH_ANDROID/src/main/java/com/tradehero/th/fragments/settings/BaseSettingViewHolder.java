package com.tradehero.th.fragments.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class BaseSettingViewHolder implements SettingViewHolder
{
    @Nullable protected DashboardPreferenceFragment preferenceFragment;

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        this.preferenceFragment = preferenceFragment;
    }

    @Override public void destroyViews()
    {
        this.preferenceFragment = null;
    }
}
