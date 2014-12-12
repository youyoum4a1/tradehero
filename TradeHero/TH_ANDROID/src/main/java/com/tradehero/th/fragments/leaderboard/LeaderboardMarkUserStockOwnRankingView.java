package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;

public class LeaderboardMarkUserStockOwnRankingView extends BaseLeaderboardMarkUserItemView
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardMarkUserStockOwnRankingView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardMarkUserStockOwnRankingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardMarkUserStockOwnRankingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void handleOpenProfileButtonClicked()
    {
        openTimeline(currentUserId.get());
    }
}
