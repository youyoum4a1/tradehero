package com.tradehero.th.activities;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import com.tradehero.common.widget.CustomDrawerToggle;
import com.tradehero.th.UIModule;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.BaseFragmentOuterElements;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.route.THRouter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Provider;
import javax.inject.Singleton;

@Module(
        addsTo = AppModule.class,
        includes = {
                UIModule.class
        },
        library = true,
        complete = false,
        overrides = true) class OneFragmentActivityModule
{
    DashboardNavigator navigator;
    Toolbar toolbar;

    @Provides DashboardNavigator provideDashboardNavigator()
    {
        return navigator;
    }

    @Provides @Singleton THRouter provideTHRouter(Context context, Provider<DashboardNavigator> navigatorProvider)
    {
        return new THRouter(context, navigatorProvider);
    }

    @Provides FragmentOuterElements provideFragmentElements()
    {
        return new BaseFragmentOuterElements();
    }

    @Provides Toolbar provideToolbar()
    {
        return toolbar;
    }

    @Provides CustomDrawerToggle provideDrawerToggle()
    {
        return null;
    }
}
