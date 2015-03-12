package com.tradehero.th.fragments.position;

import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import dagger.Module;

@Module(
        injects = {

                PositionListFragment.class,
                LeaderboardPositionListFragment.class,
                CompetitionLeaderboardPositionListFragment.class,
                PositionPartialTopView.class,
                TabbedPositionListFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentPositionModule
{
}
