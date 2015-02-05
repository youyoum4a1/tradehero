package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import javax.inject.Inject;

public class CompetitionLeaderboardMarkUserListOnGoingFragment extends CompetitionLeaderboardMarkUserListFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override @LayoutRes protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header_competition_timed;
    }

    @Override protected void initHeaderView()
    {
        super.initHeaderView();
        CompetitionLeaderboardTimedHeader headerView = (CompetitionLeaderboardTimedHeader) this.headerView;
        if (competitionDTO != null)
        {
            headerView.setCompetitionDTO(competitionDTO);
        }
        headerView.linkWith(providerDTO);
    }

    @Override protected void linkWith(@NonNull CompetitionDTO competitionDTO)
    {
        super.linkWith(competitionDTO);
        initHeaderView();
    }
}
