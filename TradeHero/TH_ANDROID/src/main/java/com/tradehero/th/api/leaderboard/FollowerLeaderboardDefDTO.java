package com.tradehero.th.api.leaderboard;

import android.content.Context;
import com.tradehero.th.R;

public class FollowerLeaderboardDefDTO extends ConnectedLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public FollowerLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefDTO.LEADERBOARD_FOLLOWER_ID;
        name = context.getString(R.string.leaderboard_community_followers);
    }
    //</editor-fold>
}
