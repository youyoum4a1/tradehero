package com.tradehero.th.fragments.leaderboard;

import android.view.View;
import com.tradehero.th.R;

public class CompetitionLeaderboardMarkUserListOnGoingFragment extends CompetitionLeaderboardMarkUserListFragment
{
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
        this.headerView.setProviderSpecificResourcesDTO(providerSpecificResourcesDTO);
        this.headerView.linkWith(providerDTO, true);
    }

    @Override public void onDestroyView()
    {
        this.headerView = null;
        super.onDestroyView();
    }

}
