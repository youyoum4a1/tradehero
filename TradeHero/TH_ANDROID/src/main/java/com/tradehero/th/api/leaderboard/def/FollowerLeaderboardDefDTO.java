package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import com.tradehero.thm.R;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class FollowerLeaderboardDefDTO extends ConnectedLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public FollowerLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefKeyKnowledge.FOLLOWER_ID;
        name = context.getString(R.string.leaderboard_community_followers);
    }
    //</editor-fold>
}
