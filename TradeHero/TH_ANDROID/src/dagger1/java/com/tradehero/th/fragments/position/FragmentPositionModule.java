package com.tradehero.th.fragments.position;

import dagger.Module;

@Module(
        injects = {

                PositionListFragment.class,
                SecurityPositionListFragment.class,
                LeaderboardPositionListFragment.class,
                CompetitionLeaderboardPositionListFragment.class,
                PositionItemAdapter.class,
        },
        library = true,
        complete = false
)
public class FragmentPositionModule
{
}
