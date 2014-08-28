package com.tradehero.th.ui;

import android.content.Context;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardResideMenu;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
                DashboardActivity.class
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
