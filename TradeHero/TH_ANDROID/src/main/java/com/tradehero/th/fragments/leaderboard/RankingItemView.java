package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:50 PM Copyright (c) TradeHero */
public class RankingItemView extends RelativeLayout
        implements DTOView<LeaderboardDefDTO>
{

    //<editor-fold desc="Constructors">
    public RankingItemView(Context context)
    {
        super(context);
    }

    public RankingItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RankingItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(LeaderboardDefDTO dto)
    {

    }
}
