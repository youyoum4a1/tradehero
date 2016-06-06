package com.androidth.general.activities;

import com.androidth.general.UIModule;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.base.BaseFragmentOuterElements;
import com.androidth.general.fragments.base.FragmentOuterElements;
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
}
