package com.tradehero.th.ui;

import com.special.residemenu.ResideMenu;
import com.tradehero.th.fragments.DashboardResideMenu;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

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
