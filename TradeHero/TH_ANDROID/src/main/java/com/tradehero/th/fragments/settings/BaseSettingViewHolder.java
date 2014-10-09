package com.tradehero.th.fragments.settings;

import android.support.annotation.StringRes;
import android.support.v4.preference.PreferenceFragment;
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

    @Nullable protected String getString(@StringRes int stringResId)
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            return preferenceFragmentCopy.getString(stringResId);
        }
        return null;
    }
}
