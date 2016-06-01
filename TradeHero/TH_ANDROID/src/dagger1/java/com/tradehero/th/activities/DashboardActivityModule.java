package com.ayondo.academy.activities;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.AbsListView;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnRecyclerViewOnScrollListener;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnScrollViewOnScrollChangedListener;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnWebViewOnScrollChangedListener;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.etiennelawlor.quickreturn.library.views.NotifyingWebView;
import com.ayondo.academy.BottomTabs;
import com.ayondo.academy.BottomTabsQuickReturnListViewListener;
import com.ayondo.academy.BottomTabsQuickReturnRecyclerViewListener;
import com.ayondo.academy.BottomTabsQuickReturnScrollViewListener;
import com.ayondo.academy.BottomTabsQuickReturnWebViewListener;
import com.ayondo.academy.UIModule;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.DashboardTabHost;
import com.ayondo.academy.fragments.NavigationAnalyticsReporter;
import com.ayondo.academy.fragments.base.DashboardFragmentOuterElements;
import com.ayondo.academy.fragments.base.FragmentOuterElements;
import com.ayondo.academy.fragments.billing.StoreScreenFragment;
import com.ayondo.academy.fragments.competition.CompetitionWebViewFragment;
import com.ayondo.academy.fragments.competition.MainCompetitionFragment;
import com.ayondo.academy.fragments.competition.ProviderVideoListFragment;
import com.ayondo.academy.fragments.discovery.DiscoveryMainFragment;
import com.ayondo.academy.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.ayondo.academy.fragments.news.NewsWebFragment;
import com.ayondo.academy.fragments.position.PositionListFragment;
import com.ayondo.academy.fragments.position.TabbedPositionListFragment;
import com.ayondo.academy.fragments.settings.SettingsFragment;
import com.ayondo.academy.fragments.social.follower.FollowersFragment;
import com.ayondo.academy.fragments.social.friend.FriendsInvitationFragment;
import com.ayondo.academy.fragments.social.hero.HeroesFragment;
import com.ayondo.academy.fragments.timeline.MeTimelineFragment;
import com.ayondo.academy.fragments.timeline.PushableTimelineFragment;
import com.ayondo.academy.fragments.trade.BuySellStockFragment;
import com.ayondo.academy.fragments.trade.FXInfoFragment;
import com.ayondo.academy.fragments.trade.FXMainFragment;
import com.ayondo.academy.fragments.trade.TradeListFragment;
import com.ayondo.academy.fragments.trending.TrendingMainFragment;
import com.ayondo.academy.fragments.updatecenter.UpdateCenterFragment;
import com.ayondo.academy.fragments.updatecenter.messageNew.MessagesCenterNewFragment;
import com.ayondo.academy.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.ayondo.academy.fragments.web.WebViewFragment;
import com.ayondo.academy.utils.dagger.AppModule;
import com.ayondo.academy.utils.metrics.ForAnalytics;
import com.ayondo.academy.utils.route.THRouter;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        addsTo = AppModule.class,
        includes = {
                UIModule.class,
        },
        injects = {
                LiveActivityUtil.class,
        },
        library = true,
        complete = false,
        overrides = true) class DashboardActivityModule
{
    DashboardNavigator navigator;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    DashboardTabHost dashboardTabHost;
    Toolbar toolbar;
    int tabHostHeight;
    //TODO Add code for Google Analytics
    //Analytics analytics;
    LiveActivityUtil liveActivityUtil;

    @Provides DashboardNavigator provideDashboardNavigator()
    {
        return navigator;
    }

    @Provides @Singleton THRouter provideTHRouter(Context context, Provider<DashboardNavigator> navigatorProvider)
    {
        THRouter router = new THRouter(context, navigatorProvider);
        router.registerRoutes(
                BuySellStockFragment.class,
                CompetitionWebViewFragment.class,
                DiscoveryMainFragment.class,
                FacebookShareActivity.class,
                FollowersFragment.class,
                FriendsInvitationFragment.class,
                FXInfoFragment.class,
                FXMainFragment.class,
                FXMainFragment.class,
                HeroesFragment.class,
                LeaderboardCommunityFragment.class,
                MainCompetitionFragment.class,
                MessagesCenterNewFragment.class,
                MeTimelineFragment.class,
                NewsWebFragment.class,
                NotificationsCenterFragment.class,
                PositionListFragment.class,
                ProviderVideoListFragment.class,
                PushableTimelineFragment.class,
                SettingsFragment.class,
                StoreScreenFragment.class,
                TabbedPositionListFragment.class,
                TradeListFragment.class,
                TrendingMainFragment.class,
                UpdateCenterFragment.class,
                WebViewFragment.class,
                LiveActivityUtil.getRoutableKYC()
        );
        DiscoveryMainFragment.registerAliases(router);
        StoreScreenFragment.registerAliases(router);
        TrendingMainFragment.registerAliases(router);
        UpdateCenterFragment.registerAliases(router);
        LiveActivityUtil.registerAliases(router);
        return router;
    }

    @Provides DrawerLayout provideDrawerLayout()
    {
        return drawerLayout;
    }

    @Provides ActionBarDrawerToggle provideActionBarDrawerToggle()
    {
        return drawerToggle;
    }

    @Provides FragmentOuterElements provideFragmentElements(DashboardFragmentOuterElements dashboardFragmentElements)
    {
        return dashboardFragmentElements;
    }

    @Provides @BottomTabs DashboardTabHost provideDashboardBottomBar()
    {
        return dashboardTabHost;
    }

    @Provides @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener provideDashboardBottomTabScrollListener()
    {
        return new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .footer(dashboardTabHost)
                .minFooterTranslation(tabHostHeight)
                .isSnappable(true)
                .build();
    }

    @Provides @BottomTabsQuickReturnScrollViewListener NotifyingScrollView.OnScrollChangedListener provideQuickReturnListViewOnScrollListener()
    {
        return new QuickReturnScrollViewOnScrollChangedListener.Builder(QuickReturnViewType.FOOTER)
                .footer(dashboardTabHost)
                .minFooterTranslation(tabHostHeight)
                .build();
    }

    @Provides @BottomTabsQuickReturnWebViewListener NotifyingWebView.OnScrollChangedListener provideQuickReturnWebViewOnScrollListener()
    {
        return new QuickReturnWebViewOnScrollChangedListener.Builder(QuickReturnViewType.FOOTER)
                .footer(dashboardTabHost)
                .minFooterTranslation(tabHostHeight)
                .build();
    }

    @Provides @BottomTabsQuickReturnRecyclerViewListener RecyclerView.OnScrollListener provideQuickReturnRecyclerViewOnScrollListener()
    {
        return new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .footer(dashboardTabHost)
                .minFooterTranslation(tabHostHeight)
                .build();
    }

    @Provides @ForAnalytics
    DashboardNavigator.DashboardFragmentWatcher provideAnalyticsReporter()
    {
        //TODO Add code for Google Analytics as second argument
        return new NavigationAnalyticsReporter(dashboardTabHost);

    }

    @Provides LiveActivityUtil provideLiveActivityUtil()
    {
        return liveActivityUtil;
    }

    @Provides Toolbar provideToolbar()
    {
        return toolbar;
    }
}
