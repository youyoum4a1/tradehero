package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import timber.log.Timber;

public class LeaderboardMarkUserOwnRankingView extends LeaderboardMarkUserItemView
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardMarkUserOwnRankingView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardMarkUserOwnRankingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardMarkUserOwnRankingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void handleOpenProfileButtonClicked()
    {
        if (viewDTO == null)
        {
            Timber.e(new Exception(), "No View DTO when trying to open profile");
        }
        else
        {
            openTimeline(viewDTO.currentUserId.get());
        }
    }
}
