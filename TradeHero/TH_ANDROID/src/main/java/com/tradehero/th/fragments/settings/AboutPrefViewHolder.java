package com.tradehero.th.fragments.settings;

import com.tradehero.th.R;
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
        preferenceFragment.getNavigator().pushFragment(AboutFragment.class);
    }
}
