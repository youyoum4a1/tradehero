package com.tradehero.th.ui;

import android.content.Context;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.th.activities.DashboardActivity;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        addsTo = UIModule.class,
        injects = {
                DashboardActivity.class
        },
        overrides = true,
        complete = false,
        library = true
)
public class UIComponents
{
    @Provides @Singleton AppContainer provideAppContainer(AppContainerImpl appContainer)
    {
        return appContainer;
    }

    @Provides @Singleton ResideMenu provideResideMenu(Context context)
    {
        return new ResideMenu(context);
    }
}
