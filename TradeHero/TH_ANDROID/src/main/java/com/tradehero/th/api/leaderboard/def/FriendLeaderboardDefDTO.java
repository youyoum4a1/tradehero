package com.ayondo.academy.api.leaderboard.def;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class FriendLeaderboardDefDTO extends LeaderboardDefDTO
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
