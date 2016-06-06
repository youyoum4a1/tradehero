package com.androidth.general.fragments.leaderboard;

import android.content.res.Resources;
import com.androidth.general.api.social.UserFriendsDTO;

public class FriendLeaderboardItemDisplayDTO
{
    public static class Social extends LeaderboardItemDisplayDTO
    {
        public UserFriendsDTO userFriendsDTO;

        public Social(Resources resources, UserFriendsDTO userFriendsDTO)
        {
            super(resources);
            this.userFriendsDTO = userFriendsDTO;
        }
    }

    public static class CallToAction extends LeaderboardItemDisplayDTO
    {
        protected CallToAction(Resources resources)
        {
            super(resources);
        }
    }
}
