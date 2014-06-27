package com.tradehero.th.utils;

import android.app.AlertDialog;
import android.webkit.WebView;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
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
import com.tradehero.th.persistence.competition.ProviderCache;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.ShadowWebView;
import org.robolectric.shadows.ShadowWebViewNew;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricMavenTestRunner.class)
@Config(shadows = ShadowWebViewNew.class)
public class THRouterTest
{
    private DashboardNavigator dashboardNavigator;

    @Inject THRouter thRouter;
    @Inject ProviderCache providerCache;
    @Inject ProviderUtil providerUtil;
    @Inject CurrentUserId currentUserId;

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
        thRouter.open("user/108805/portfolio/883124");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(PositionListFragment.class);
    }

    @Test public void shouldGoToTradeHistoryOfGivenPosition()
    {
        // user/:userId/portfolio/:portfolioId/position/:positionId
        thRouter.open("user/108805/portfolio/883124/position/1610238");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(TradeListFragment.class);
    }
    //endregion

    //region Store
    @Test public void shouldOpenStoreScreenForStoreRoute()
    {
        thRouter.open("store");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(StoreScreenFragment.class);

        AlertDialog resetPortfolioDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(resetPortfolioDialog).isNull();

    }

    @Test public void shouldOpenStoreAndResetPortfolioDialog() throws Throwable
    {
        thRouter.open("reset-portfolio");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(StoreScreenFragment.class);

        AlertDialog resetPortfolioDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(resetPortfolioDialog).isNotNull();
        assertThat(resetPortfolioDialog.isShowing()).isTrue();

        CharSequence dialogTitle = shadowOf(resetPortfolioDialog).getTitle();
        assertThat(dialogTitle).isEqualTo(Robolectric.application.getString(R.string.store_billing_loading_info_window_title));
    }

    @Test public void shouldOpenStoreAndResetPortfolioDialogFullUrl()
    {
        thRouter.open("store/reset-portfolio");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(StoreScreenFragment.class);

        // ensure that there is no unwanted toast due to clicking on a unexpected list item
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast()).isNull();
        ShadowHandler.runMainLooperToEndOfTasks();

        AlertDialog resetPortfolioDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(resetPortfolioDialog.isShowing()).isTrue();

        CharSequence dialogTitle = shadowOf(resetPortfolioDialog).getTitle();
        assertThat(dialogTitle).isEqualTo(Robolectric.application.getString(R.string.store_billing_loading_info_window_title));
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
        ProviderDTO providerDTO = new ProviderDTO();
        providerDTO.id = 23;
        ProviderId providerId = providerDTO.getProviderId();
        providerCache.put(providerId, providerDTO);

        currentUserId.set(108805);

        thRouter.open("providers-enroll/" + providerId.key);

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(CompetitionWebViewFragment.class);

        CompetitionWebViewFragment competitionWebViewFragment = (CompetitionWebViewFragment) dashboardNavigator.getCurrentFragment();
        assertThat(competitionWebViewFragment.getWebView()).isNotNull();

        ShadowWebView shadowWebView = shadowOf(competitionWebViewFragment.getWebView());
        String landingPage = providerUtil.getLandingPage(providerId, currentUserId.toUserBaseKey());
        assertThat(shadowWebView.getLastLoadedUrl()).isEqualTo(landingPage);
    }

    // we won't test this anymore since this feature is deprecated
    @Deprecated
    public void shouldOpenProviderEnrollmentWithSpecificPage()
    {
        // providers-enroll/:providerId/pages/:encodedUrl
        thRouter.open("providers-enroll/22/pages/http:%2F%2Fgoogle.com");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(CompetitionWebViewFragment.class);

        CompetitionWebViewFragment competitionWebViewFragment = (CompetitionWebViewFragment) dashboardNavigator.getCurrentFragment();
        WebView webView = competitionWebViewFragment.getWebView();
        assertThat(webView).isNotNull();

        ShadowWebView shadowWebView = shadowOf(webView);
        assertThat(shadowWebView.getLastLoadedUrl()).isEqualTo("http://google.com");
    }
    //endregion

    @Test public void shouldOpenReferFriendScreen()
    {
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