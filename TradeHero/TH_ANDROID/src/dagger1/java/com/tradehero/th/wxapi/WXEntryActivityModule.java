package com.ayondo.academy.wxapi;

import android.app.Activity;
import android.content.Context;
import android.widget.AbsListView;
import com.ayondo.academy.BottomTabsQuickReturnListViewListener;
import com.ayondo.academy.UIModule;
import com.ayondo.academy.fragments.DashboardNavigator;
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
) class WXEntryActivityModule
{
    WXEntryActivity activity;

    public WXEntryActivityModule(WXEntryActivity activity)
    {
        this.activity = activity;
    }

    @Provides Activity provideActivity()
    {
        return activity;
    }

    @Provides DashboardNavigator provideDashboardNavigator()
    {
        throw new IllegalStateException("No navigator available when in Wechat share");
    }

    @Provides @Singleton THRouter provideTHRouter(Context context, Provider<DashboardNavigator> navigatorProvider)
    {
        throw new IllegalStateException("No router available when in Wechat share");
    }

    @Provides @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener provideDashboardBottomTabScrollListener()
    {
        return new AbsListView.OnScrollListener()
        {
            @Override public void onScrollStateChanged(AbsListView absListView, int i)
            {
            }

            @Override public void onScroll(AbsListView absListView, int i, int i2, int i3)
            {
            }
        };
    }
}
