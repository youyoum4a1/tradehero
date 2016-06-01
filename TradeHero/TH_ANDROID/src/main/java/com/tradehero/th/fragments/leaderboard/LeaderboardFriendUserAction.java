package com.ayondo.academy.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.social.UserFriendsDTO;

public class LeaderboardFriendUserAction
{
    @NonNull public final UserFriendsDTO userFriendsDTO;

    public LeaderboardFriendUserAction(@NonNull UserFriendsDTO userFriendsDTO)
    {
        this.userFriendsDTO = userFriendsDTO;
    }
}