package com.tradehero.th.fragments.settings;

import android.preference.Preference;
import org.jetbrains.annotations.NotNull;

abstract public class OneSettingViewHolder extends BaseSettingViewHolder
{
    protected Preference clickablePref;

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);

        clickablePref = preferenceFragment.findPreference(
                preferenceFragment.getString(getStringKeyResId()));
        if (clickablePref != null)
        {
            clickablePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handlePrefClicked();
                    return true;
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
    abstract protected void handlePrefClicked();

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
