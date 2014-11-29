package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.UserFriendsDTO;

public class FriendLeaderboardSocialUserDTO extends FriendLeaderboardUserDTO
{
    @NonNull public UserFriendsDTO userFriendsDTO;

    //<editor-fold desc="Constructors">
    public FriendLeaderboardSocialUserDTO(@NonNull UserFriendsDTO userFriendsDTO)
    {
        super(false);
        this.userFriendsDTO = userFriendsDTO;
    }
    //</editor-fold>
}
