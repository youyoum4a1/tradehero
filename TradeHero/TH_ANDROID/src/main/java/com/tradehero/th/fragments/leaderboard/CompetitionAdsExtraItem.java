package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.adapters.WrapperRecyclerAdapter;
import com.tradehero.th.api.competition.AdDTO;

public class CompetitionAdsExtraItem extends LeaderboardItemDisplayDTO implements WrapperRecyclerAdapter.ExtraItem
{
    public final AdDTO adDTO;

    public CompetitionAdsExtraItem(AdDTO adDTO)
    {
        this.adDTO = adDTO;
    }

    @Override public int getViewType()
    {
        return 99;
    }

    @Override public boolean equals(Object o)
    {
        return this == o || o instanceof CompetitionAdsExtraItem;
    }
}