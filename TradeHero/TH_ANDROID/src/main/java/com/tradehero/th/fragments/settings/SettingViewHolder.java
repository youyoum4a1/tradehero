package com.tradehero.th.fragments.settings;

import android.preference.Preference;
import org.jetbrains.annotations.NotNull;

public interface SettingViewHolder
{
    void initViews(@NotNull DashboardPreferenceFragment preferenceFragment);
    void destroyViews();
    boolean isUnread();
    Preference getPreference();
}
