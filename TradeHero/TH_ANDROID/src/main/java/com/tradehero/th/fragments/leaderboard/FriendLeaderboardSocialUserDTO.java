package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.api.social.UserFriendsDTO;

public class FriendLeaderboardSocialUserDTO extends FriendLeaderboardUserDTO
{
    public UserFriendsDTO userFriendsDTO;

    //<editor-fold desc="Constructors">
    public FriendLeaderboardSocialUserDTO(UserFriendsDTO userFriendsDTO)
    {
        super(false);
        this.userFriendsDTO = userFriendsDTO;
    }
    //</editor-fold>
}
