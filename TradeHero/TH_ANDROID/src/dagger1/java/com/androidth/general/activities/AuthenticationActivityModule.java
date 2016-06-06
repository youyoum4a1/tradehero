package com.androidth.general.activities;

import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.utils.dagger.AppModule;
import dagger.Module;
import dagger.Provides;

@Module(
        addsTo = AppModule.class,
        library = true,
        complete = false,
        overrides = true
) class AuthenticationActivityModule
{
    DashboardNavigator navigator;

    @Provides DashboardNavigator provideDashboardNavigator()
    {
        return navigator;
    }
}
