package com.tradehero.th.fragments.leaderboard.main;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.leaderboard.AbstractLeaderboardDefView;

public class CommunityLeaderboardDefView extends AbstractLeaderboardDefView
        implements DTOView<CommunityPageDTO>
{
    private CommunityPageDTO communityPageDTO;

    //<editor-fold desc="Constructors">
    public CommunityLeaderboardDefView(Context context)
    {
        super(context);
    }

    public CommunityLeaderboardDefView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CommunityLeaderboardDefView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(CommunityPageDTO dto)
    {
        this.communityPageDTO = dto;
        if (communityPageDTO != null)
        {
            linkWith(((LeaderboardDefCommunityPageDTO) communityPageDTO).leaderboardDefDTO, true);
        }
    }
}
