package com.tradehero.th.fragments.settings;

import com.tradehero.th.R;
import javax.inject.Inject;

public class AlipaySettingViewHolder extends OneSettingViewHolder
{
    //<editor-fold desc="Constructors">
    @Inject public AlipaySettingViewHolder()
    {
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_alipay;
    }

    @Override protected void handlePrefClicked()
    {
        preferenceFragment.getNavigator().pushFragment(SettingsAlipayFragment.class);
    }
}
