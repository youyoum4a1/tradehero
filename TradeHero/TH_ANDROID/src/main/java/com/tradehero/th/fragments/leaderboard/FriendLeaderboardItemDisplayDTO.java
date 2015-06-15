package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.api.social.UserFriendsDTO;

public class FriendLeaderboardItemDisplayDTO
{
    public static class Social extends LeaderboardItemDisplayDTO
    {
        public UserFriendsDTO userFriendsDTO;

        public Social(UserFriendsDTO userFriendsDTO)
        {
            this.userFriendsDTO = userFriendsDTO;
        }
    }

    public static class CallToAction extends LeaderboardItemDisplayDTO
    {

    }
}
