package com.androidth.general.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.UserFriendsDTO;

public class LeaderboardFriendUserAction
{
    @NonNull public final UserFriendsDTO userFriendsDTO;

    public LeaderboardFriendUserAction(@NonNull UserFriendsDTO userFriendsDTO)
    {
        this.userFriendsDTO = userFriendsDTO;
    }
}