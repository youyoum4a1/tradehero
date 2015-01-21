package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.Pair;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.key.CompetitionId;
import javax.inject.Inject;
import rx.Observer;

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

    @Override public void onDestroyView()
    {
        this.headerView = null;
        super.onDestroyView();
    }

    @Override protected Observer<Pair<CompetitionId, CompetitionDTO>> createCompetitionObserver()
    {
        return new CompetitionOnGoingObserver();
    }

    protected class CompetitionOnGoingObserver extends CompetitionObserver
    {
        @Override public void onNext(Pair<CompetitionId, CompetitionDTO> pair)
        {
            super.onNext(pair);
            initHeaderView();
        }
    }
}
