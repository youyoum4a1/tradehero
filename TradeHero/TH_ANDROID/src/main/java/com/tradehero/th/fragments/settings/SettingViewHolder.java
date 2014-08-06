package com.tradehero.th.fragments.settings;

import org.jetbrains.annotations.NotNull;

public interface SettingViewHolder
{
    void initViews(@NotNull DashboardPreferenceFragment preferenceFragment);
    void destroyViews();
}
