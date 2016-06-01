package com.ayondo.academy.fragments.competition;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.AbsListView;
import com.ayondo.academyRobolectric;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.competition.AdDTO;
import com.ayondo.academy.api.competition.CompetitionDTOList;
import com.ayondo.academy.api.competition.ProviderDTO;
import com.ayondo.academy.api.competition.ProviderDisplayCellDTOList;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.competition.ProviderUtil;
import com.ayondo.academy.api.competition.key.ProviderDisplayCellListKey;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.competition.zone.dto.CompetitionZoneWizardDTO;
import com.ayondo.academy.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.ayondo.academy.fragments.security.ProviderSecurityListRxFragment;
import com.ayondo.academy.persistence.competition.CompetitionListCacheRx;
import com.ayondo.academy.persistence.competition.ProviderCacheRx;
import com.ayondo.academy.persistence.competition.ProviderDisplayCellListCacheRx;
import java.util.ArrayList;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowWebView;
import org.robolectric.shadows.ShadowWebViewNew;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(THRobolectricTestRunner.class)
@Config(shadows = ShadowWebViewNew.class, constants = BuildConfig.class)
public class MainCompetitionFragmentTest
{
    private static final String TEST_ADS_WEB_URL = "http://www.google.com";
    private static final String TEST_WIZARD_WEB_URL = "http://www.apple.com";
    @Inject DashboardNavigator dashboardNavigator;
    @Inject ProviderCacheRx providerCache;
    @Inject ProviderDisplayCellListCacheRx providerDisplayCellListCache;
    @Inject CompetitionListCacheRx competitionListCache;
    @Inject ProviderUtil providerUtil;
    @Inject CurrentUserId currentUserId;
    private ProviderId providerId;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);

        providerId = new ProviderId(3423);
        // creating mock object for providerDTO
        ProviderDTO mockProviderDTO = new ProviderDTO();
        mockProviderDTO.id = providerId.key;
        mockProviderDTO.associatedPortfolio = new PortfolioCompactDTO();
        mockProviderDTO.associatedPortfolio.id = 24;
        mockProviderDTO.associatedPortfolio.userId = 25;

        AdDTO adDTO = new AdDTO();
        adDTO.redirectUrl = TEST_ADS_WEB_URL;
        mockProviderDTO.advertisements = new ArrayList<>();
        mockProviderDTO.advertisements.add(adDTO);

        providerCache.onNext(providerId, mockProviderDTO);

        CompetitionDTOList competitionDTOList = new CompetitionDTOList();
        competitionListCache.onNext(providerId, competitionDTOList);

        ProviderDisplayCellDTOList providerDisplayCellDTOList = new ProviderDisplayCellDTOList();
        providerDisplayCellListCache.onNext(new ProviderDisplayCellListKey(providerId), providerDisplayCellDTOList);

    }

    @Test public void shouldBeAbleToNavigateToMainCompetitionFragmentWithOutApplicablePortfolioId()
    {
        Bundle args = new Bundle();

        ProviderId providerId = new ProviderId(3423);
        MainCompetitionFragment.putProviderId(args, providerId);

        MainCompetitionFragment mainCompetition = dashboardNavigator.pushFragment(MainCompetitionFragment.class, args);

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(MainCompetitionFragment.class);
        assertThat(mainCompetition).isNotNull();
        assertThat(mainCompetition.listView).isNotNull();
    }

    @Test public void shouldGoToProviderSecurityListAfterClickOnTradeNowButton()
    {
        Bundle args = new Bundle();

        MainCompetitionFragment.putProviderId(args, providerId);
        MainCompetitionFragment mainCompetitionFragment = dashboardNavigator.pushFragment(MainCompetitionFragment.class, args);

        mainCompetitionFragment.btnTradeNow.performClick();
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(ProviderSecurityListRxFragment.class);
    }

    @Test public void shouldGoToWebFragmentAfterClickOnAds() throws InterruptedException
    {
        Robolectric.getBackgroundThreadScheduler().pause();
        Bundle args = new Bundle();
        MainCompetitionFragment.putProviderId(args, providerId);

        ProviderDTO providerDTO = providerCache.getCachedValue(providerId);
        // make sure that we has advertisement before testing Ads Cell
        assertThat(providerDTO.hasAdvertisement()).isTrue();

        MainCompetitionFragment mainCompetitionFragment = dashboardNavigator.pushFragment(MainCompetitionFragment.class, args);


        AbsListView competitionListView = mainCompetitionFragment.listView;
        assertThat(competitionListView).isNotNull();

        Robolectric.getBackgroundThreadScheduler().unPause();

        THRobolectric.runBgUiTasks(3);

        CompetitionZoneListItemAdapter competitionListAdapter = (CompetitionZoneListItemAdapter) competitionListView.getAdapter();
        assertThat(competitionListAdapter).isNotNull();

        int firstAdsButtonPosition = -1;

        for (int i = 0; i < competitionListAdapter.getCount(); ++i)
        {
            if (competitionListAdapter.getItemViewType(i) == CompetitionZoneListItemAdapter.ITEM_TYPE_ADS)
            {
                firstAdsButtonPosition = i;
                break;
            }
        }
        assertThat(firstAdsButtonPosition).isGreaterThan(-1);

        competitionListView.performItemClick(
                competitionListAdapter.getView(firstAdsButtonPosition, null, null),
                firstAdsButtonPosition,
                competitionListAdapter.getItemId(firstAdsButtonPosition));
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(CompetitionWebViewFragment.class);

        CompetitionWebViewFragment competitionWebViewFragment = (CompetitionWebViewFragment) dashboardNavigator.getCurrentFragment();

        WebView webView = competitionWebViewFragment.getWebView();
        ShadowWebView shadowWebView = shadowOf(webView);
        assertThat(webView).isNotNull();
        assertThat(shadowWebView.getLastLoadedUrl()).isEqualTo(providerUtil.appendUserId(TEST_ADS_WEB_URL, '&'));
    }

    @Test public void shouldGoToCompetitionPortfolioAfterClickOnCompetitionPortfolio() throws InterruptedException
    {
        Bundle args = new Bundle();
        MainCompetitionFragment.putProviderId(args, providerId);

        MainCompetitionFragment mainCompetitionFragment = dashboardNavigator.pushFragment(MainCompetitionFragment.class, args);

        AbsListView competitionListView = mainCompetitionFragment.listView;
        assertThat(competitionListView).isNotNull();

        CompetitionZoneListItemAdapter competitionListAdapter = (CompetitionZoneListItemAdapter) competitionListView.getAdapter();
        assertThat(competitionListAdapter).isNotNull();

        THRobolectric.runBgUiTasks(3);

        int firstPortfolioButtonPosition = -1;

        for (int i = 0; i < competitionListAdapter.getCount(); ++i)
        {
            if (competitionListAdapter.getItemViewType(i) == CompetitionZoneListItemAdapter.ITEM_TYPE_PORTFOLIO)
            {
                firstPortfolioButtonPosition = i;
                break;
            }
        }

        assertThat(firstPortfolioButtonPosition).isGreaterThan(-1);

        competitionListView.performItemClick(
                competitionListAdapter.getView(firstPortfolioButtonPosition, null, null),
                firstPortfolioButtonPosition,
                competitionListAdapter.getItemId(firstPortfolioButtonPosition));
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(CompetitionLeaderboardPositionListFragment.class);

        CompetitionLeaderboardPositionListFragment competitionLeaderboardPositionListFragment =
                (CompetitionLeaderboardPositionListFragment) dashboardNavigator.getCurrentFragment();

        assertThat(competitionLeaderboardPositionListFragment.getProviderId()).isEqualTo(providerId);


    }

    @Test public void shouldGoToTradeQuestPageAfterClickOnWizardCellWhenWizardUrlIsSetToTradeQuestUrl() throws InterruptedException
    {
        ProviderDTO providerDTO = providerCache.getCachedValue(providerId);
        providerDTO.wizardUrl = TEST_WIZARD_WEB_URL;
        providerCache.onNext(providerId, providerDTO);
        shouldGoToCorrectWebPageAfterClickOnWizardCell(TEST_WIZARD_WEB_URL);
    }

    @Test public void shouldGoToProviderWizardPageAfterClickOnWizardCell() throws InterruptedException
    {
        // we do not hardcoded on client anymore for generating competition url from providerId
        // but I would like to test it anyway
        ProviderDTO providerDTO = providerCache.getCachedValue(providerId);
        providerDTO.wizardUrl = null;
        providerCache.onNext(providerId, providerDTO);

        String expectedWizardPage = providerUtil.getWizardPage(providerId);
        shouldGoToCorrectWebPageAfterClickOnWizardCell(expectedWizardPage);
    }

    private void shouldGoToCorrectWebPageAfterClickOnWizardCell(String webLink) throws InterruptedException
    {
        Robolectric.getBackgroundThreadScheduler().pause();
        Bundle args = new Bundle();
        MainCompetitionFragment.putProviderId(args, providerId);

        // make sure that we have wizard before proceed testing it
        ProviderDTO providerDTO = providerCache.getCachedValue(providerId);
        assertThat(providerDTO.hasWizard()).isTrue();

        MainCompetitionFragment mainCompetitionFragment = dashboardNavigator.pushFragment(MainCompetitionFragment.class, args);

        AbsListView competitionListView = mainCompetitionFragment.listView;
        assertThat(competitionListView).isNotNull();

        CompetitionZoneListItemAdapter competitionListAdapter = (CompetitionZoneListItemAdapter) competitionListView.getAdapter();
        assertThat(competitionListAdapter).isNotNull();

        Robolectric.getBackgroundThreadScheduler().unPause();

        THRobolectric.runBgUiTasks(3);

        int firstWizardButtonPosition = -1;

        for (int i = 0; i < competitionListAdapter.getCount(); ++i)
        {
            if (competitionListAdapter.getItemViewType(i) == CompetitionZoneListItemAdapter.ITEM_TYPE_ZONE_ITEM
                    && competitionListAdapter.getItem(i) instanceof CompetitionZoneWizardDTO)
            {
                firstWizardButtonPosition = i;
                break;
            }
        }

        assertThat(firstWizardButtonPosition).isGreaterThan(-1);

        competitionListView.performItemClick(
                competitionListAdapter.getView(firstWizardButtonPosition, null, null),
                firstWizardButtonPosition,
                competitionListAdapter.getItemId(firstWizardButtonPosition));
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(CompetitionWebViewFragment.class);

        CompetitionWebViewFragment competitionWebViewFragment = (CompetitionWebViewFragment) dashboardNavigator.getCurrentFragment();

        WebView webView = competitionWebViewFragment.getWebView();
        ShadowWebView shadowWebView = shadowOf(webView);
        assertThat(webView).isNotNull();
        assertThat(shadowWebView.getLastLoadedUrl()).isEqualTo(webLink);
    }
}