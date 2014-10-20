package com.tradehero.th.fragments.settings;

import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import dagger.Lazy;
import javax.inject.Inject;

public class AboutPrefViewHolder extends OneSettingViewHolder
{
    private final Lazy<DashboardNavigator> dashboardNavigatorLazy;

    //<editor-fold desc="Constructors">
    @Inject public AboutPrefViewHolder(Lazy<DashboardNavigator> dashboardNavigatorLazy)
    {
        this.dashboardNavigatorLazy = dashboardNavigatorLazy;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_misc_about;
    }

    @Override protected void handlePrefClicked()
    {
        dashboardNavigatorLazy.get().pushFragment(AboutFragment.class);
    }
}
