package com.tradehero.th.fragments.position;

import com.tradehero.th.fragments.position.partial.PositionPartialBottomClosedView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomInPeriodViewHolder;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomOpenView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import dagger.Component;

/**
 * Created by tho on 9/9/2014.
 */
@Component
public interface FragmentPositionComponent
{
    void injectPositionListFragment(PositionListFragment target);
    void injectLeaderboardPositionListFragment(LeaderboardPositionListFragment target);
    void injectCompetitionLeaderboardPositionListFragment(CompetitionLeaderboardPositionListFragment target);

    void injectPositionPartialTopView(PositionPartialTopView target);
    void injectPositionPartialBottomClosedView(PositionPartialBottomClosedView target);
    void injectPositionPartialBottomOpenView(PositionPartialBottomOpenView target);
    void injectPositionLockedView(PositionLockedView target);
    void injectPositionPartialBottomInPeriodViewHolder(PositionPartialBottomInPeriodViewHolder target);
}
