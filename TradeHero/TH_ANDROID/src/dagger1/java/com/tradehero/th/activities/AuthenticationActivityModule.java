package com.ayondo.academy.activities;

import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.utils.dagger.AppModule;
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
