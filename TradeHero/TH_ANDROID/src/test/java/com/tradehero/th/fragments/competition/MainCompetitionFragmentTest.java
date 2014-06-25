package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.persistence.competition.ProviderCache;
import java.util.ArrayList;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricMavenTestRunner.class)
public class MainCompetitionFragmentTest
{
    private DashboardNavigator dashboardNavigator;
    @Inject ProviderCache providerCache;
    private ProviderId providerId;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();

        providerId = new ProviderId(23);
        // creating mock object for providerDTO
        ProviderDTO mockProviderDTO = new ProviderDTO();
        mockProviderDTO.id = providerId.key;
        mockProviderDTO.associatedPortfolio = mock(PortfolioCompactDTO.class);

        AdDTO adDTO = new AdDTO();
        adDTO.redirectUrl = "http://www.google.com";
        mockProviderDTO.advertisements = new ArrayList<>();
        mockProviderDTO.advertisements.add(adDTO);

        providerCache.put(providerId, mockProviderDTO);
    }

    @Test public void shouldAbleToNavigateToMainCompetitionFragmentWithOutApplicablePortfolioId()
    {
        Bundle args = new Bundle();

        ProviderId providerId = new ProviderId(23);
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

        AbsListView competitionListView = mainCompetitionFragment.listView;
        ListAdapter competitionListAdapter = competitionListView.getAdapter();

        int firstTradeNowButtonPosition = -1;

        for (int i = 0; i < competitionListAdapter.getCount(); ++i)
        {
            if (competitionListAdapter.getItemViewType(i) == CompetitionZoneListItemAdapter.ITEM_TYPE_TRADE_NOW)
            {
                firstTradeNowButtonPosition = i;
                break;
            }
        }
        assertThat(firstTradeNowButtonPosition).isGreaterThan(-1);

        competitionListView.performItemClick(
                competitionListAdapter.getView(firstTradeNowButtonPosition, null, null),
                firstTradeNowButtonPosition,
                competitionListAdapter.getItemId(firstTradeNowButtonPosition));
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(ProviderSecurityListFragment.class);
    }

    @Test public void shouldGoToWebFragmentAfterClickOnAds()
    {
        Bundle args = new Bundle();
        MainCompetitionFragment.putProviderId(args, providerId);

        ProviderDTO providerDTO = providerCache.get(providerId);
        // make sure that we has advertisement before testing Ads Cell
        assertThat(providerDTO.hasAdvertisement()).isTrue();

        MainCompetitionFragment mainCompetitionFragment = dashboardNavigator.pushFragment(MainCompetitionFragment.class, args);

        AbsListView competitionListView = mainCompetitionFragment.listView;
        assertThat(competitionListView).isNotNull();

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

        competitionListView.performItemClick(
                competitionListAdapter.getView(firstAdsButtonPosition, null, null),
                firstAdsButtonPosition,
                competitionListAdapter.getItemId(firstAdsButtonPosition));
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(CompetitionWebViewFragment.class);
    }
}