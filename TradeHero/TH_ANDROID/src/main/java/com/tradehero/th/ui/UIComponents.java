package com.tradehero.th.ui;

import com.special.ResideMenu.ResideMenu;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardResideMenu;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
<<<<<<< HEAD
        //addsTo = UIModule.class,
=======
>>>>>>> develop2.0
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
