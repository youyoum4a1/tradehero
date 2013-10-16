package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.fragments.base.ItemListFragment;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 12:34 PM Copyright (c) TradeHero */
public class LeaderboardRankingListViewFragment extends ItemListFragment<LeaderboardDefDTO>
{
    private LeaderboardRankingAdapter leaderboardRankingAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return super.onCreateView(inflater, container,
                savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override public Loader<List<LeaderboardDefDTO>> onCreateLoader(int i, Bundle bundle)
    {
        return leaderboardRankingAdapter == null ? null : leaderboardRankingAdapter.getLoader();
    }
}
