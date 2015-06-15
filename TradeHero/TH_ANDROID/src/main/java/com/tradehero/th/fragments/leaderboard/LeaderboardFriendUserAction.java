package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.UserFriendsDTO;

public class LeaderboardFriendUserAction
{
    @NonNull public final UserFriendsDTO userFriendsDTO;

    public LeaderboardFriendUserAction(@NonNull UserFriendsDTO userFriendsDTO)
    {
        this.userFriendsDTO = userFriendsDTO;
    }
}