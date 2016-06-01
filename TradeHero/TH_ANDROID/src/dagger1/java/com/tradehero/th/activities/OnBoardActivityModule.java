package com.ayondo.academy.activities;

import android.content.Context;
import com.ayondo.academy.UIModule;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.base.BaseFragmentOuterElements;
import com.ayondo.academy.fragments.base.FragmentOuterElements;
import com.ayondo.academy.utils.dagger.AppModule;
import com.ayondo.academy.utils.route.THRouter;
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
        overrides = true
) class OnBoardActivityModule
{
    DashboardNavigator navigator;

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
}
