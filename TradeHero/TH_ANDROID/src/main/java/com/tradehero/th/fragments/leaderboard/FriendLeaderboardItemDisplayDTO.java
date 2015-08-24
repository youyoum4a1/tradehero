package com.tradehero.th.fragments.leaderboard;

import android.content.res.Resources;
import com.tradehero.th.api.social.UserFriendsDTO;

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
