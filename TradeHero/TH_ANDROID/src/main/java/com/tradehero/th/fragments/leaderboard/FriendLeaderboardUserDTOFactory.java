package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.social.UserFriendsDTO;

interface FriendLeaderboardUserDTOFactory
{
    @NonNull FriendLeaderboardUserDTO create(@NonNull LeaderboardUserDTO leaderboardUserDTO);
    @NonNull FriendLeaderboardUserDTO create(@NonNull UserFriendsDTO userFriendsDTO);
}
