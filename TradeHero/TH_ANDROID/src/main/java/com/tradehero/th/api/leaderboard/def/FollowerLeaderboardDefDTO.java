package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class FollowerLeaderboardDefDTO extends ConnectedLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public FollowerLeaderboardDefDTO(@NonNull Context context)
    {
        super();
        id = LeaderboardDefKeyKnowledge.FOLLOWER_ID;
        name = context.getString(R.string.leaderboard_community_followers);
    }
    //</editor-fold>
}
