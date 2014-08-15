package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;

public class LeaderboardMarkUserOwnRankingView extends LeaderboardMarkUserItemView
{

    public LeaderboardMarkUserOwnRankingView(Context context)
    {
        super(context);
    }

    public LeaderboardMarkUserOwnRankingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardMarkUserOwnRankingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void handleOpenProfileButtonClicked()
    {
        openTimeline(currentUserId.get());
    }
}
