package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.widget.ListAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 6:45 PM Copyright (c) TradeHero */
public class LeaderboardDefTimePeriodListAdapter extends LeaderboardDefListAdapter
{
    public LeaderboardDefTimePeriodListAdapter(Context context, LayoutInflater inflater, List<LeaderboardDefDTO> items, int layoutResourceId)
    {
        super(context, inflater, items, layoutResourceId);
    }
}
