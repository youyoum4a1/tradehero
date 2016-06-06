package com.androidth.general.fragments.leaderboard;

import android.content.res.Resources;
import com.androidth.general.adapters.WrapperRecyclerAdapter;
import com.androidth.general.api.competition.AdDTO;

public class CompetitionAdsExtraItem extends LeaderboardItemDisplayDTO implements WrapperRecyclerAdapter.ExtraItem
{
    public static final int VIEW_TYPE_ADS = 99;
    public final AdDTO adDTO;

    public CompetitionAdsExtraItem(Resources resources, AdDTO adDTO)
    {
        super(resources);
        this.adDTO = adDTO;
    }

    @Override public int getViewType()
    {
        return VIEW_TYPE_ADS;
    }

    @Override public boolean equals(Object o)
    {
        return this == o || o instanceof CompetitionAdsExtraItem;
    }
}