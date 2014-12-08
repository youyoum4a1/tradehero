package com.tradehero.th.fragments.competition;

import com.tradehero.th.fragments.competition.zone.CompetitionZoneLeaderboardListItemView;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneListItemView;
import com.tradehero.th.fragments.competition.zone.CompetitionZonePortfolioView;
import com.tradehero.th.fragments.competition.zone.CompetitionZonePrizePoolView;
import dagger.Component;

@Component
public interface FragmentCompetitionComponent
{
    void injectCompetitionWebViewFragment(CompetitionWebViewFragment competitionWebViewFragment);
    void injectCompetitionEnrollmentWebViewFragment(CompetitionEnrollmentWebViewFragment competitionEnrollmentWebViewFragment);
    void injectCompetitionZoneListItemView(CompetitionZoneListItemView competitionZoneListItemView);
    void injectCompetitionZoneLeaderboardListItemView(CompetitionZoneLeaderboardListItemView competitionZoneLeaderboardListItemView);
    void injectCompetitionZonePortfolioView(CompetitionZonePortfolioView competitionZonePortfolioView);
    void injectCompetitionZonePrizePoolView(CompetitionZonePrizePoolView competitionZonePrizePoolView);
    void injectCompetitionPreseasonDialogFragment(CompetitionPreseasonDialogFragment competitionPreseasonDialogFragment);
    void injectMainCompetitionFragment(MainCompetitionFragment mainCompetitionFragment);
    void injectProviderVideoListFragment(ProviderVideoListFragment providerVideoListFragment);
    void injectProviderVideoListItemView(ProviderVideoListItemView providerVideoListItemView);
    void injectProviderSecurityListFragment(ProviderSecurityListFragment providerSecurityListFragment);
    void injectAdView(AdView adView);
}
