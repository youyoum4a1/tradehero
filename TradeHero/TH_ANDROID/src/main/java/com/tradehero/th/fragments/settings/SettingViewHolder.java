package com.tradehero.th.fragments.settings;

import android.preference.Preference;
import android.support.annotation.NonNull;

public interface SettingViewHolder
{
    void initViews(@NonNull DashboardPreferenceFragment preferenceFragment);
    void destroyViews();
    boolean isUnread();
    Preference getPreference();
}
