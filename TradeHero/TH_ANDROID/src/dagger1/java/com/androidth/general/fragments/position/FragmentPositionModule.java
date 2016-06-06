package com.androidth.general.fragments.position;

import com.androidth.general.fragments.position.partial.PositionPartialTopView;
import com.androidth.general.fragments.position.view.PositionView;
import dagger.Module;

@Module(
        injects = {

                PositionListFragment.class,
                SecurityPositionListFragment.class,
                LeaderboardPositionListFragment.class,
                CompetitionLeaderboardPositionListFragment.class,
                PositionPartialTopView.class,
                TabbedPositionListFragment.class,
                StocksMainPositionListFragment.class,
                FXMainPositionListFragment.class,
                PositionItemAdapter.class,
                PositionView.class,
        },
        library = true,
        complete = false
)
public class FragmentPositionModule
{
}
