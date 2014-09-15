package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import com.tradehero.th2.R;
import javax.inject.Inject;

public class CompetitionLeaderboardMarkUserListOnGoingFragment extends CompetitionLeaderboardMarkUserListFragment
{
    // DON'T DELETE FOLLOWING LINE, this dummy injection is used to trick dagger to generate InjectAdapter for this class
    @Inject Context ignored;

    @Override protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header_competition_timed;
    }

    @Override protected void initHeaderView()
    {
        super.initHeaderView();
        CompetitionLeaderboardTimedHeader headerView = (CompetitionLeaderboardTimedHeader) this.headerView;
        headerView.setCompetitionDTO(competitionDTO);
        headerView.linkWith(providerDTO, true);
    }

    @Override public void onDestroyView()
    {
        this.headerView = null;
        super.onDestroyView();
    }

}
