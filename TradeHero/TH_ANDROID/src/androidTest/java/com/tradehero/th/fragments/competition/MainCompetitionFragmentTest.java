package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.AbsListView;
import com.tradehero.THRobolectric;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDisplayCellDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderDisplayCellListKey;
import com.tradehero.th.api.competition.specific.ProviderSpecificKnowledgeDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneWizardDTO;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.persistence.competition.CompetitionListCache;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.competition.ProviderDisplayCellListCache;
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
import static org.robolectric.Robolectric.shadowOf;

@RunWith(THRobolectricTestRunner.class)
@Config(shadows = ShadowWebViewNew.class)
public class MainCompetitionFragmentTest
{
    private static final String TEST_ADS_WEB_URL = "http://www.google.com";
    private static final String TEST_WIZARD_WEB_URL = "http://www.apple.com";
    @Inject DashboardNavigator dashboardNavigator;
    @Inject ProviderCacheRx providerCache;
    @Inject ProviderDisplayCellListCache providerDisplayCellListCache;
    @Inject CompetitionListCache competitionListCache;
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
        competitionListCache.put(providerId, competitionDTOList);

        ProviderDisplayCellDTOList providerDisplayCellDTOList = new ProviderDisplayCellDTOList();
        providerDisplayCellListCache.put(new ProviderDisplayCellListKey(providerId), providerDisplayCellDTOList);

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
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(ProviderSecurityListFragment.class);
    }

    @Test public void shouldGoToWebFragmentAfterClickOnAds() throws InterruptedException
    {
        Robolectric.getBackgroundScheduler().pause();
        Bundle args = new Bundle();
        MainCompetitionFragment.putProviderId(args, providerId);

        ProviderDTO providerDTO = providerCache.getValue(providerId);
        // make sure that we has advertisement before testing Ads Cell
        assertThat(providerDTO.hasAdvertisement()).isTrue();

        MainCompetitionFragment mainCompetitionFragment = dashboardNavigator.pushFragment(MainCompetitionFragment.class, args);


        AbsListView competitionListView = mainCompetitionFragment.listView;
        assertThat(competitionListView).isNotNull();

        Robolectric.getBackgroundScheduler().unPause();

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
        ProviderDTO providerDTO = providerCache.getValue(providerId);
        providerDTO.wizardUrl = TEST_WIZARD_WEB_URL;
        providerCache.onNext(providerId, providerDTO);
        shouldGoToCorrectWebPageAfterClickOnWizardCell(TEST_WIZARD_WEB_URL);
    }

    @Test public void shouldGoToProviderWizardPageAfterClickOnWizardCell() throws InterruptedException
    {
        // we do not hardcoded on client anymore for generating competition url from providerId
        // but I would like to test it anyway
        ProviderDTO providerDTO = providerCache.getValue(providerId);
        providerDTO.wizardUrl = null;
        // for enabling wizard cell
        providerDTO.specificKnowledge = new ProviderSpecificKnowledgeDTO();
        providerDTO.specificKnowledge.hasWizard = true;
        providerCache.onNext(providerId, providerDTO);

        String expectedWizardPage = providerUtil.getWizardPage(providerId);
        shouldGoToCorrectWebPageAfterClickOnWizardCell(expectedWizardPage);
    }

    private void shouldGoToCorrectWebPageAfterClickOnWizardCell(String webLink) throws InterruptedException
    {
        Robolectric.getBackgroundScheduler().pause();
        Bundle args = new Bundle();
        MainCompetitionFragment.putProviderId(args, providerId);

        // make sure that we have wizard before proceed testing it
        ProviderDTO providerDTO = providerCache.getValue(providerId);
        assertThat(providerDTO.hasWizard()).isTrue();

        MainCompetitionFragment mainCompetitionFragment = dashboardNavigator.pushFragment(MainCompetitionFragment.class, args);

        AbsListView competitionListView = mainCompetitionFragment.listView;
        assertThat(competitionListView).isNotNull();

        CompetitionZoneListItemAdapter competitionListAdapter = (CompetitionZoneListItemAdapter) competitionListView.getAdapter();
        assertThat(competitionListAdapter).isNotNull();

        Robolectric.getBackgroundScheduler().unPause();

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