package com.tradehero.th.fragments.settings;

import com.tradehero.th.R;
import javax.inject.Inject;

public class PayPalSettingViewHolder extends OneSettingViewHolder
{
    //<editor-fold desc="Constructors">
    @Inject public PayPalSettingViewHolder()
    {
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_paypal;
    }

    @Override protected void handlePrefClicked()
    {
        preferenceFragment.getNavigator().pushFragment(SettingsPayPalFragment.class);
    }
}
