package com.androidth.general.activities;

import android.content.Context;
import com.androidth.general.UIModule;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.base.BaseFragmentOuterElements;
import com.androidth.general.fragments.base.FragmentOuterElements;
import com.androidth.general.utils.dagger.AppModule;
import com.androidth.general.utils.route.THRouter;
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
