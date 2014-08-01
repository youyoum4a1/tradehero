package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.View;
import com.tradehero.th.R;
import javax.inject.Inject;

public class CompetitionLeaderboardMarkUserListOnGoingFragment extends CompetitionLeaderboardMarkUserListFragment
{
    // DON'T DELETE FOLLOWING LINE, this dummy injection is used to trick dagger to generate InjectAdapter for this class
    @Inject Context ignored;

    protected CompetitionLeaderboardTimedHeader headerView;

    @Override protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header_competition_timed;
    }

    @Override protected void initHeaderView(View headerView)
    {
        super.initHeaderView(headerView);
        this.headerView = (CompetitionLeaderboardTimedHeader) headerView;
        this.headerView.setCompetitionDTO(competitionDTO);
        this.headerView.linkWith(providerDTO, true);
    }

    @Override public void onDestroyView()
    {
        this.headerView = null;
        super.onDestroyView();
    }

}
