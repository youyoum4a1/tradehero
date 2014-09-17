package com.tradehero.th.ui;

import com.special.residemenu.ResideMenu;
import com.tradehero.th.fragments.DashboardResideMenu;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class UIComponents
{
    @Provides @Singleton AppContainer provideAppContainer(AppContainerImpl appContainer)
    {
        return appContainer;
    }

    @Provides @Singleton ResideMenu provideResideMenu(DashboardResideMenu dashboardResideMenu)
    {
        return dashboardResideMenu;
    }
}
