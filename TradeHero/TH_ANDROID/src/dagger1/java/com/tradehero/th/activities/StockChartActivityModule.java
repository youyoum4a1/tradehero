package com.ayondo.academy.activities;

import com.ayondo.academy.UIModule;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.base.BaseFragmentOuterElements;
import com.ayondo.academy.fragments.base.FragmentOuterElements;
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
