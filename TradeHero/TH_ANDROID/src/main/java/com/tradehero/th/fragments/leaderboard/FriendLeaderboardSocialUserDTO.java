package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.api.social.UserFriendsDTO;
import org.jetbrains.annotations.NotNull;

public class FriendLeaderboardSocialUserDTO extends FriendLeaderboardUserDTO
{
    @NotNull public UserFriendsDTO userFriendsDTO;

    //<editor-fold desc="Constructors">
    public FriendLeaderboardSocialUserDTO(@NotNull UserFriendsDTO userFriendsDTO)
    {
        super(false);
        this.userFriendsDTO = userFriendsDTO;
    }
    //</editor-fold>
}
