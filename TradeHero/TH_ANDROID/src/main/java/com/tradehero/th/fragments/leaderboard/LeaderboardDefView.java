package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;

public class LeaderboardDefView extends AbstractLeaderboardDefView
        implements DTOView<LeaderboardDefDTO>
{
    private LeaderboardDefDTO leaderboardDefDTO;

    //<editor-fold desc="Constructors">
    public LeaderboardDefView(Context context)
    {
        super(context);
    }

    public LeaderboardDefView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardDefView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(LeaderboardDefDTO dto)
    {
        this.leaderboardDefDTO = dto;
        linkWith(leaderboardDefDTO, true);
    }
}
