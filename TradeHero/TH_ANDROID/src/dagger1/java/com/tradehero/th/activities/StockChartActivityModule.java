package com.tradehero.th.activities;

import com.tradehero.common.widget.CustomDrawerToggle;
import com.tradehero.th.UIModule;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.BaseFragmentOuterElements;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                UIModule.class
        },
        library = true,
        complete = false
) class StockChartActivityModule
{
    @Provides FragmentOuterElements provideFragmentElements()
    {
        return new BaseFragmentOuterElements();
    }

    @Provides DashboardNavigator provideNavigator()
    {
        return null;
    }

    @Provides CustomDrawerToggle provideDrawerToggle()
    {
        return null;
    }
}
