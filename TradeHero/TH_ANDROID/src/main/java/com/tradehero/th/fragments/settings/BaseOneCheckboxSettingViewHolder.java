package com.tradehero.th.fragments.settings;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class BaseOneCheckboxSettingViewHolder extends BaseSettingViewHolder
{
    @Nullable protected CheckBoxPreference clickablePref;

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
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

    abstract protected int getStringKeyResId();
    abstract protected boolean changeStatus(boolean enable);
}
