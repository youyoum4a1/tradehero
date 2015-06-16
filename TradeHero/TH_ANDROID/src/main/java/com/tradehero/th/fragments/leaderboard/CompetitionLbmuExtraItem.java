package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.adapters.WrapperRecyclerAdapter;

public class CompetitionLbmuExtraItem extends LeaderboardItemDisplayDTO implements WrapperRecyclerAdapter.ExtraItem
{
    @Override public int getViewType()
    {
        return 99;
    }
}
