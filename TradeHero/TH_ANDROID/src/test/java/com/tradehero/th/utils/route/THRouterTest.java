package com.ayondo.academy.utils.route;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.webkit.WebView;
import com.ayondo.academyRobolectric;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.R;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.competition.ProviderDTO;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.competition.ProviderUtil;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTO;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTOList;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.billing.StoreScreenFragment;
import com.ayondo.academy.fragments.competition.CompetitionWebViewFragment;
import com.ayondo.academy.fragments.competition.MainCompetitionFragment;
import com.ayondo.academy.fragments.competition.ProviderVideoListFragment;
import com.ayondo.academy.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.ayondo.academy.fragments.position.PositionListFragment;
import com.ayondo.academy.fragments.settings.SettingsFragment;
import com.ayondo.academy.fragments.social.friend.FriendsInvitationFragment;
import com.ayondo.academy.fragments.timeline.MeTimelineFragment;
import com.ayondo.academy.fragments.timeline.PushableTimelineFragment;
import com.ayondo.academy.fragments.trade.BuySellStockFragment;
import com.ayondo.academy.fragments.trade.TradeListFragment;
import com.ayondo.academy.fragments.trending.TrendingStockFragment;
import com.ayondo.academy.fragments.updatecenter.UpdateCenterFragment;
import com.ayondo.academy.fragments.updatecenter.messageNew.MessagesCenterNewFragment;
import com.ayondo.academy.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.ayondo.academy.persistence.competition.ProviderCacheRx;
import com.ayondo.academy.persistence.portfolio.PortfolioCompactListCacheRx;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.ShadowWebView;
import org.robolectric.shadows.ShadowWebViewNew;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(THRobolectricTestRunner.class)
@Config(shadows = ShadowWebViewNew.class, constants = BuildConfig.class)
public class THRouterTest
{
    @Inject DashboardNavigator dashboardNavigator;
    @Inject THRouter thRouter;
    @Inject ProviderCacheRx providerCache;
    @Inject ProviderUtil providerUtil;
    @Inject CurrentUserId currentUserId;
    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;

    @Before public void setUp()
    {
        DashboardActivityExtended activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
        currentUserId.set(34);
        thRouter.setContext(activity);
    }

    @After public void tearDown()
    {
        portfolioCompactListCache.invalidateAll();
        dashboardNavigator.popFragment();
        dashboardNavigator = null;

        thRouter.setContext(null);
    }

    @NonNull private PortfolioCompactDTOList createCurrentUserPortfolios()
    {
        PortfolioCompactDTOList created = new PortfolioCompactDTOList();
        PortfolioCompactDTO defaultPortfolio = new PortfolioCompactDTO();
        defaultPortfolio.id = 1;
        defaultPortfolio.userId = currentUserId.get();
        created.add(defaultPortfolio);
        PortfolioCompactDTO otherPortfolio = new PortfolioCompactDTO();
        otherPortfolio.id = 2;
        otherPortfolio.userId = currentUserId.get();
        created.add(otherPortfolio);
        return created;
    }

    //region Timeline
    @Test public void shouldOpenUserTimelineForUserProfileRoute()
    {
        // suspend to prevent loader from being run along with uiThread in robolectric
        Robolectric.getBackgroundThreadScheduler().pause();
        thRouter.open("user/108805");

        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(PushableTimelineFragment.class);
    }

    @Test public void shouldOpenOwnTimelineForMeRoute()
    {
        // suspend to prevent loader from being run along with uiThread in robolectric
        Robolectric.getBackgroundThreadScheduler().pause();
        thRouter.open("user/me");

        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
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
        ShadowAlertDialog.reset();
        thRouter.open("store");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(StoreScreenFragment.class);

        AlertDialog resetPortfolioDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(resetPortfolioDialog).isNull();
    }

    @Test public void shouldOpenStoreAndResetPortfolioDialog() throws Throwable
    {
        portfolioCompactListCache.onNext(currentUserId.toUserBaseKey(), createCurrentUserPortfolios());
        thRouter.open("reset-portfolio");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(StoreScreenFragment.class);

        THRobolectric.runBgUiTasks(3);

        AlertDialog resetPortfolioDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(resetPortfolioDialog).isNotNull();
        assertThat(resetPortfolioDialog.isShowing()).isTrue();

        CharSequence dialogTitle = shadowOf(resetPortfolioDialog).getTitle();
        assertThat(dialogTitle).isEqualTo(RuntimeEnvironment.application.getString(R.string.store_billing_loading_info_window_title));
    }

    @Test public void shouldOpenStoreAndResetPortfolioDialogFullUrl()
    {
        portfolioCompactListCache.onNext(currentUserId.toUserBaseKey(), createCurrentUserPortfolios());
        thRouter.open("store/reset-portfolio");
        ShadowToast.reset();

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(StoreScreenFragment.class);

        // ensure that there is no unwanted toast due to clicking on a unexpected list item
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast()).isNull();
        ShadowHandler.runMainLooperToEndOfTasks();

        AlertDialog resetPortfolioDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(resetPortfolioDialog.isShowing()).isTrue();

        CharSequence dialogTitle = shadowOf(resetPortfolioDialog).getTitle();
        assertThat(dialogTitle).isEqualTo(RuntimeEnvironment.application.getString(R.string.store_billing_loading_info_window_title));
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

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(BuySellStockFragment.class);
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

    @Test public void shouldOpenProviderVideoListFragment()
    {
        thRouter.open("providers/23/helpVideos");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(ProviderVideoListFragment.class);
    }

    @Test public void shouldOpenProviderEnrollmentScreen()
    {
        ProviderDTO providerDTO = new ProviderDTO();
        providerDTO.id = 23;
        ProviderId providerId = providerDTO.getProviderId();
        providerCache.onNext(providerId, providerDTO);

        currentUserId.set(108805);

        thRouter.open("providers-enroll/" + providerId.key);

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(CompetitionWebViewFragment.class);

        CompetitionWebViewFragment competitionWebViewFragment = (CompetitionWebViewFragment) dashboardNavigator.getCurrentFragment();
        assertThat(competitionWebViewFragment.getWebView()).isNotNull();

        ShadowWebView shadowWebView = shadowOf(competitionWebViewFragment.getWebView());
        String landingPage = providerUtil.getLandingPage(providerId);
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
        assertThat(updateCenterFragment.getCurrentFragment()).isInstanceOf(MessagesCenterNewFragment.class);
    }

    @Test public void shouldOpenTrendingScreen()
    {
        thRouter.open("trending-securities");
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(TrendingStockFragment.class);
    }
}