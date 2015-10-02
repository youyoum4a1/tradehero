package com.tradehero.th.fragments.position;

import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionView;
import dagger.Module;

@Module(
        injects = {

                PositionListFragment.class,
                SecurityPositionListFragment.class,
                LeaderboardPositionListFragment.class,
                CompetitionLeaderboardPositionListFragment.class,
                PositionPartialTopView.class,
                PositionItemAdapter.class,
                PositionView.class,
        },
        library = true,
        complete = false
)
public class FragmentPositionModule
{
}
