package com.tradehero.th.utils;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.SocialFriendsFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class THRouterTest
{
    private DashboardActivity activity;
    private DashboardNavigator dashboardNavigator;
    private THRouter thRouter;

    @Before public void setUp()
    {
        activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();

        thRouter = new THRouter(activity);
    }

    //region Timeline
    @Test public void shouldOpenUserTimelineForUserProfileRoute()
    {
        thRouter.mapFragment(THRouter.USER_TIMELINE, PushableTimelineFragment.class);

        thRouter.open("user/108805");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(PushableTimelineFragment.class);
    }

    @Test public void shouldOpenOwnTimelineForMeRoute()
    {
        thRouter.mapFragment(THRouter.USER_ME, MeTimelineFragment.class);

        thRouter.open("user/me");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(MeTimelineFragment.class);
    }
    //endregion

    //region Portfolios & Positions
    @Test public void shouldGoToPositionListOfGivenPortfolio()
    {
        thRouter.mapFragment(THRouter.PORTFOLIO_POSITION, PositionListFragment.class);

        thRouter.open("/user/108805/portfolio/883124");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(PositionListFragment.class);
    }

    @Test public void shouldGoToTradeHistoryOfGivenPosition()
    {
        thRouter.mapFragment(THRouter.POSITION_TRADE_HISTORY, TradeListFragment.class);

        thRouter.open("/user/108805/portfolio/883124/position/1610238");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(TradeListFragment.class);
    }
    //endregion

    //region Store
    @Test public void shouldOpenStoreScreenForStoreRoute()
    {
        thRouter.mapFragment(THRouter.STORE, StoreScreenFragment.class);

        thRouter.open("store");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(StoreScreenFragment.class);
    }

    @Test public void shouldOpenStoreAndResetPortfolioDialog()
    {
        // have something to say
        thRouter.mapFragment(THRouter.STORE_RESET_PORTFOLIO, null);

        thRouter.mapFragment(THRouter.RESET_PORTFOLIO, null);
    }
    //endregion

    //region Settings
    @Test public void shouldGoToSettingScreen()
    {
        thRouter.mapFragment(THRouter.SETTING, SettingsFragment.class);

        thRouter.open("settings");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(SettingsFragment.class);
    }
    //endregion

    //region Security
    @Test public void shouldOpenSecurityScreen()
    {
        thRouter.mapFragment(THRouter.SECURITY, BuySellFragment.class);
        thRouter.open("security/4_NASDAQ_AAPL");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(BuySellFragment.class);
    }
    //endregion

    //region Providers
    @Test public void shouldOpenProviderListScreen()
    {
        thRouter.mapFragment(THRouter.PROVIDER_LIST, LeaderboardCommunityFragment.class);
    }

    @Test public void shouldOpenProviderScreen()
    {
        thRouter.mapFragment(THRouter.PROVIDER, MainCompetitionFragment.class);
        thRouter.open("providers/23");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(MainCompetitionFragment.class);
    }

    @Test public void shouldOpenProviderEnrollmentScreen()
    {
        thRouter.mapFragment(THRouter.PROVIDER_ENROLL, WebViewFragment.class);
        thRouter.open("providers-enroll/23");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(WebViewFragment.class);
        // assertWebPage...
    }

    @Test public void shouldOpenProviderEnrollmentWithSpecificPage()
    {
        thRouter.mapFragment(THRouter.PROVIDER_ENROLL_WITH_PAGE, WebViewFragment.class);
        thRouter.open("providers-enroll/22/pages/http:%2F%2Fgoogle.com");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(WebViewFragment.class);
        // assertWebPage should be google.com
    }
    //endregion

    @Test public void shouldOpenReferFriendScreen()
    {
        thRouter.mapFragment(THRouter.REFER_FRIENDS, SocialFriendsFragment.class);
        thRouter.open("refer-friends");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(SocialFriendsFragment.class);
    }

    @Test public void shouldOpenNotificationCenter()
    {
        thRouter.mapFragment(THRouter.NOTIFICATION, UpdateCenterFragment.class);

        thRouter.open("notifications");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(UpdateCenterFragment.class);
    }

    @Test public void shouldOpenMessageScreen()
    {
        thRouter.mapFragment(THRouter.MESSAGE, MessagesCenterFragment.class);

        thRouter.open("messages");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(MessagesCenterFragment.class);
    }

    @Test public void shouldOpenTrendingScreen()
    {
        thRouter.mapFragment(THRouter.TRENDING, TrendingFragment.class);

        thRouter.open("trending-securities");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(TrendingFragment.class);
    }

}