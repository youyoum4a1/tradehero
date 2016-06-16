package com.androidth.general.activities;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.AbsListView;

import com.androidth.general.BottomTabs;
import com.androidth.general.BottomTabsQuickReturnListViewListener;
import com.androidth.general.BottomTabsQuickReturnRecyclerViewListener;
import com.androidth.general.BottomTabsQuickReturnScrollViewListener;
import com.androidth.general.BottomTabsQuickReturnWebViewListener;
import com.androidth.general.UIModule;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.DashboardTabHost;
import com.androidth.general.fragments.NavigationAnalyticsReporter;
import com.androidth.general.fragments.base.BaseLiveFragmentUtil;
import com.androidth.general.fragments.base.DashboardFragmentOuterElements;
import com.androidth.general.fragments.base.FragmentOuterElements;
import com.androidth.general.fragments.base.TrendingLiveFragmentUtil;
import com.androidth.general.fragments.billing.StoreScreenFragment;
import com.androidth.general.fragments.competition.CompetitionWebViewFragment;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.fragments.competition.ProviderVideoListFragment;
import com.androidth.general.fragments.discovery.DiscoveryMainFragment;
import com.androidth.general.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.androidth.general.fragments.live.DatePickerDialogFragment;
import com.androidth.general.fragments.live.LiveCallToActionFragment;
import com.androidth.general.fragments.live.LiveSignUpMainFragment;
import com.androidth.general.fragments.live.VerifyPhoneDialogFragment;
import com.androidth.general.fragments.live.ayondo.LiveSignUpStep1AyondoFragment;
import com.androidth.general.fragments.news.NewsWebFragment;
import com.androidth.general.fragments.position.PositionListFragment;
import com.androidth.general.fragments.position.TabbedPositionListFragment;
import com.androidth.general.fragments.settings.SettingsFragment;
import com.androidth.general.fragments.social.follower.FollowersFragment;
import com.androidth.general.fragments.social.friend.FriendsInvitationFragment;
import com.androidth.general.fragments.social.hero.HeroesFragment;
import com.androidth.general.fragments.timeline.MeTimelineFragment;
import com.androidth.general.fragments.timeline.PushableTimelineFragment;
import com.androidth.general.fragments.trade.BuySellStockFragment;
import com.androidth.general.fragments.trade.FXInfoFragment;
import com.androidth.general.fragments.trade.FXMainFragment;
import com.androidth.general.fragments.trade.TradeListFragment;
import com.androidth.general.fragments.trending.TrendingMainFragment;
import com.androidth.general.fragments.updatecenter.UpdateCenterFragment;
import com.androidth.general.fragments.updatecenter.messageNew.MessagesCenterNewFragment;
import com.androidth.general.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.androidth.general.fragments.web.WebViewFragment;
import com.androidth.general.utils.dagger.AppModule;
import com.androidth.general.utils.metrics.ForAnalytics;
import com.androidth.general.utils.route.THRouter;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnRecyclerViewOnScrollListener;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnScrollViewOnScrollChangedListener;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnWebViewOnScrollChangedListener;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.etiennelawlor.quickreturn.library.views.NotifyingWebView;

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
//                LiveSignUpStep1AyondoFragment.class,
//                SignUpLiveActivity.class,
                IdentityPromptActivity.class,
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
