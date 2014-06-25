package com.tradehero.th.fragments.competition;

import com.tradehero.th.fragments.competition.zone.CompetitionZoneLeaderboardListItemView;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneListItemTradeNowView;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneListItemView;
import com.tradehero.th.fragments.competition.zone.CompetitionZonePortfolioView;
import dagger.Module;

@Module(
        staticInjections =
                {
                },
        injects =
                {
                        CompetitionZoneListItemAdapter.class,
                        CompetitionZoneListItemView.class,
                        CompetitionZoneLeaderboardListItemView.class,
                        CompetitionZoneListItemTradeNowView.class,
                        CompetitionZonePortfolioView.class,
                        MainCompetitionFragment.class,
                        ProviderVideoListFragment.class,
                        ProviderVideoListItem.class,
                        ProviderSecurityListFragment.class,
                        CompetitionWebViewFragment.class
                },
        complete = false,
        library = true
)
public class CompetitionModule
{
    public CompetitionModule()
    {
    }
}
