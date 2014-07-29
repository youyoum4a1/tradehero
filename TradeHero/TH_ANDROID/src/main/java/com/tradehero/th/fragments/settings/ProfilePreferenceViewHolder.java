package com.tradehero.th.fragments.settings;

import android.preference.Preference;
import com.tradehero.th.R;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ProfilePreferenceViewHolder implements SettingViewHolder
{
    protected DashboardPreferenceFragment preferenceFragment;
    protected Preference profilePref;

    //<editor-fold desc="Constructors">
    @Inject public ProfilePreferenceViewHolder()
    {
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        this.preferenceFragment = preferenceFragment;

        profilePref = preferenceFragment.findPreference(
                preferenceFragment.getString(R.string.key_settings_primary_profile));
        if (profilePref != null)
        {
            profilePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleProfileClicked();
                    return true;
                }
            });
        }
    }

    @Override public void destroyViews()
    {
        this.preferenceFragment = null;
        this.profilePref = null;
    }

    private void handleProfileClicked()
    {
        preferenceFragment.getNavigator().pushFragment(SettingsProfileFragment.class);
    }

}
