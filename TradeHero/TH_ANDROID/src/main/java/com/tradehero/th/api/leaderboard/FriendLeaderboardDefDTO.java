package com.tradehero.th.api.leaderboard;

import android.content.Context;
import com.tradehero.th.R;

public class FriendLeaderboardDefDTO extends ConnectedLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public FriendLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefDTO.LEADERBOARD_FRIEND_ID;
        name = context.getString(R.string.leaderboard_community_friends);
    }
    //</editor-fold>
}
