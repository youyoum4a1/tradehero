package com.tradehero.th.activities;

import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.utils.dagger.AppModule;
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
