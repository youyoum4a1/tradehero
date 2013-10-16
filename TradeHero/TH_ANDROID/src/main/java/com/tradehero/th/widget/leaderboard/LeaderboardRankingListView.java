package com.tradehero.th.widget.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.widget.PullToUpdateFromLoaderListView;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 3:09 PM Copyright (c) TradeHero */
public class LeaderboardRankingListView extends PullToUpdateFromLoaderListView
{
    public LeaderboardRankingListView(Context context)
    {
        super(context);
    }

    public LeaderboardRankingListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardRankingListView(Context context, Mode mode)
    {
        super(context, mode);
    }

    public LeaderboardRankingListView(Context context, Mode mode, AnimationStyle style)
    {
        super(context, mode, style);
    }
}
