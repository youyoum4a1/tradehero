package com.tradehero.th.utils;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class THRouterTest
{
    public static final String POSITION_TRADE_HISTORY = "user/:userId/portfolio/:portfolioId/position/:positionId";
    public static final String STORE_RESET_PORTFOLIO = "store/reset-portfolio";
    public static final String RESET_PORTFOLIO = "reset-portfolio";
    public static final String REFER_FRIENDS = "refer-friends";

    private DashboardNavigator dashboardNavigator;
    @Inject THRouter thRouter;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
        thRouter.setContext(activity);
    }

    //region Timeline
    @Test public void shouldOpenUserTimelineForUserProfileRoute()
    {
        // suspend to prevent loader from being run along with uiThread in robolectric
        Robolectric.getBackgroundScheduler().pause();
        thRouter.open("user/108805");

        Robolectric.runBackgroundTasks();
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(PushableTimelineFragment.class);
    }

    @Test public void shouldOpenOwnTimelineForMeRoute()
    {
        // suspend to prevent loader from being run along with uiThread in robolectric
        Robolectric.getBackgroundScheduler().pause();
        thRouter.open("user/me");

        Robolectric.runBackgroundTasks();
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(MeTimelineFragment.class);
    }
    //endregion

    //region Portfolios & Positions
    @Test public void shouldGoToPositionListOfGivenPortfolio()
    {
        thRouter.open("/user/108805/portfolio/883124");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(PositionListFragment.class);
    }

    @Test public void shouldGoToTradeHistoryOfGivenPosition()
    {
        thRouter.mapFragment(POSITION_TRADE_HISTORY, TradeListFragment.class);

        thRouter.open("/user/108805/portfolio/883124/position/1610238");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(TradeListFragment.class);
    }
    //endregion

    //region Store
    @Test public void shouldOpenStoreScreenForStoreRoute()
    {
        thRouter.open("store");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(StoreScreenFragment.class);
    }

    @Test public void shouldOpenStoreAndResetPortfolioDialog()
    {
        // have something to say
        thRouter.mapFragment(STORE_RESET_PORTFOLIO, null);
        thRouter.mapFragment(RESET_PORTFOLIO, null);

        assert(false);
    }
    //endregion

    //region Settings
    @Test public void shouldGoToSettingScreen()
    {
        thRouter.open("settings");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(SettingsFragment.class);
    }
    //endregion

    //region Security
    @Test public void shouldOpenSecurityScreen()
    {
        thRouter.open("security/4_NASDAQ_AAPL");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(BuySellFragment.class);
    }
    //endregion

    //region Providers
    @Test public void shouldOpenProviderListScreen()
    {
        thRouter.open("providers");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(LeaderboardCommunityFragment.class);
    }

    @Test public void shouldOpenProviderScreen()
    {
        thRouter.open("providers/23");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(MainCompetitionFragment.class);
    }

    @Test public void shouldOpenProviderEnrollmentScreen()
    {
        // providers-enroll/:providerId
        //thRouter.mapFragment(PROVIDER_ENROLL, WebViewFragment.class);
        thRouter.open("providers-enroll/23");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(WebViewFragment.class);
        // assertWebPage...
    }

    @Test public void shouldOpenProviderEnrollmentWithSpecificPage()
    {
        // providers-enroll/:providerId/pages/:encodedUrl
        //thRouter.mapFragment(PROVIDER_ENROLL_WITH_PAGE, WebViewFragment.class);
        thRouter.open("providers-enroll/22/pages/http:%2F%2Fgoogle.com");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(WebViewFragment.class);
        // assertWebPage should be google.com
    }
    //endregion

    @Test public void shouldOpenReferFriendScreen()
    {
        thRouter.mapFragment(REFER_FRIENDS, FriendsInvitationFragment.class);
        thRouter.open("refer-friends");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(FriendsInvitationFragment.class);
    }

    @Test public void shouldOpenNotificationCenter()
    {
        thRouter.open("notifications");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(UpdateCenterFragment.class);

        UpdateCenterFragment updateCenterFragment = (UpdateCenterFragment) dashboardNavigator.getCurrentFragment();
        assertThat(updateCenterFragment.getCurrentFragment()).isInstanceOf(NotificationsCenterFragment.class);
    }

    @Test public void shouldOpenMessageScreen()
    {
        thRouter.open("messages");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(UpdateCenterFragment.class);

        UpdateCenterFragment updateCenterFragment = (UpdateCenterFragment) dashboardNavigator.getCurrentFragment();
        assertThat(updateCenterFragment.getCurrentFragment()).isInstanceOf(MessagesCenterFragment.class);
    }

    @Test public void shouldOpenTrendingScreen()
    {
        thRouter.open("trending-securities");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(TrendingFragment.class);
    }

}