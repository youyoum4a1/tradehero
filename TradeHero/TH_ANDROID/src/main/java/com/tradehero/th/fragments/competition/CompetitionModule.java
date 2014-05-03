package com.tradehero.th.fragments.competition;

import com.tradehero.th.fragments.competition.zone.CompetitionZoneLeaderboardListItemView;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneListItemTradeNowView;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneListItemView;
import com.tradehero.th.fragments.competition.zone.CompetitionZonePortfolioView;
import com.tradehero.th.persistence.competition.ProviderListRetrievedMilestone;
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
                        ProviderListRetrievedMilestone.class,
                        ProviderVideoListFragment.class,
                        ProviderVideoListItem.class,
                        ProviderSecurityListFragment.class,
                },
        complete = false,
        library = true
)
public class CompetitionModule
{
    public static final String TAG = CompetitionModule.class.getSimpleName();

    public CompetitionModule()
    {
    }
}
