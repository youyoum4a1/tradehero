package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class FriendLeaderboardDefDTO extends ConnectedLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public FriendLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefKeyKnowledge.FRIEND_ID;
        name = context.getString(R.string.leaderboard_community_friends);
    }
    //</editor-fold>
}
