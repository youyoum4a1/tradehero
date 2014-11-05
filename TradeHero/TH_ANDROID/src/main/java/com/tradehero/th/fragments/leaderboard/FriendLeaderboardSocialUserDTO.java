package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.api.social.UserFriendsDTO;
import android.support.annotation.NonNull;

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
