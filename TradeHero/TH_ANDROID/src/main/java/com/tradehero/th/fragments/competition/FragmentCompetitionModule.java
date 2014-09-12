package com.tradehero.th.fragments.competition;

import com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneLeaderboardListItemView;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneListItemView;
import com.tradehero.th.fragments.competition.zone.CompetitionZonePortfolioView;
import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                MacquarieWarrantItemViewAdapter.class,
                CompetitionWebViewFragment.class,
                CompetitionZoneListItemAdapter.class,
                CompetitionZoneListItemView.class,
                CompetitionZoneLeaderboardListItemView.class,
                CompetitionZonePortfolioView.class,
                MainCompetitionFragment.class,
                ProviderVideoListFragment.class,
                ProviderVideoListItemView.class,
                ProviderSecurityListFragment.class,
                AdView.class,
        },
        library = true,
        complete = false
)
public class FragmentCompetitionModule
{
}
