package com.tradehero.th.api.leaderboard.def;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class FriendLeaderboardDefDTO extends ConnectedLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public FriendLeaderboardDefDTO(@NonNull Resources resources)
    {
        super();
        id = LeaderboardDefKeyKnowledge.FRIEND_ID;
        name = resources.getString(R.string.leaderboard_community_friends);
    }
    //</editor-fold>
}
