package com.tradehero.th.fragments.settings;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

abstract public class BaseOneCheckboxSettingViewHolder extends BaseSettingViewHolder
{
    @Nullable protected CheckBoxPreference clickablePref;

    @Override public void initViews(@NonNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        clickablePref = (CheckBoxPreference) preferenceFragment.findPreference(
                preferenceFragment.getString(getStringKeyResId()));
        if (clickablePref != null)
        {
            clickablePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changeStatus((boolean) newValue);
                }
            });
        }
    }

    @Override public void destroyViews()
    {
        this.clickablePref = null;
        super.destroyViews();
    }

    @StringRes abstract protected int getStringKeyResId();
    abstract protected boolean changeStatus(boolean enable);

    @Override public boolean isUnread()
    {
        return (clickablePref instanceof ShowUnreadPreference)
                && !((ShowUnreadPreference) clickablePref).isVisited();
    }

    @Override public Preference getPreference()
    {
        return clickablePref;
    }
}
