package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class InviteFriendsLeaderboardDefDTO extends ConnectedLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public InviteFriendsLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefKeyKnowledge.INVITE_FRIENDS_ID;
        //name = "ad";
    }
    //</editor-fold>
}
