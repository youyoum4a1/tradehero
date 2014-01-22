package com.tradehero.th.fragments.competition;

import com.tradehero.th.persistence.competition.ProviderListRetrievedMilestone;
import dagger.Module;

/**
 * Created by xavier on 1/13/14.
 */
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
