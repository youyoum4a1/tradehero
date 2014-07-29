package com.tradehero.th.fragments.settings;

import com.tradehero.th.R;
import javax.inject.Inject;

public class ReferralCodeViewHolder extends OneSettingViewHolder
{
    //<editor-fold desc="Constructors">
    @Inject public ReferralCodeViewHolder()
    {
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_referral_code;
    }

    @Override protected void handlePrefClicked()
    {
        preferenceFragment.getNavigator().pushFragment(SettingsReferralCodeFragment.class);
    }
}
