package com.tradehero.th.fragments.settings;

import com.tradehero.th2.R;
import javax.inject.Inject;

public class AboutPrefViewHolder extends OneSettingViewHolder
{
    //<editor-fold desc="Constructors">
    @Inject public AboutPrefViewHolder()
    {
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_misc_about;
    }

    @Override protected void handlePrefClicked()
    {
        DashboardPreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            preferenceFragment.getNavigator().pushFragment(AboutFragment.class);
        }
    }
}
