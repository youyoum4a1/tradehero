package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import com.tradehero.th.R;
import javax.inject.Inject;

public class CompetitionLeaderboardMarkUserListOnGoingFragment extends CompetitionLeaderboardMarkUserListFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

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
