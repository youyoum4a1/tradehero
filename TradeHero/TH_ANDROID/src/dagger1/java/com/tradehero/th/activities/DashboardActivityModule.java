package com.tradehero.th.activities;

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
import com.tradehero.th.BottomTabs;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.BottomTabsQuickReturnRecyclerViewListener;
import com.tradehero.th.BottomTabsQuickReturnScrollViewListener;
import com.tradehero.th.BottomTabsQuickReturnWebViewListener;
import com.tradehero.th.UIModule;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.base.DashboardFragmentOuterElements;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.competition.ProviderVideoListFragment;
import com.tradehero.th.fragments.discovery.DiscoveryMainFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.news.NewsWebFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.position.TabbedPositionListFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.follower.FollowersFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.social.hero.HeroesFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.trade.FXInfoFragment;
import com.tradehero.th.fragments.trade.FXMainFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.messageNew.MessagesCenterNewFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.route.THRouter;

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

    /*@Provides @ForAnalytics
    DashboardNavigator.DashboardFragmentWatcher provideAnalyticsReporter()
    {
        //TODO Add code for Google Analytics
        return new NavigationAnalyticsReporter(analytics, dashboardTabHost);

    }*/

    @Provides LiveActivityUtil provideLiveActivityUtil()
    {
        return liveActivityUtil;
    }

    @Provides Toolbar provideToolbar()
    {
        return toolbar;
    }
}
