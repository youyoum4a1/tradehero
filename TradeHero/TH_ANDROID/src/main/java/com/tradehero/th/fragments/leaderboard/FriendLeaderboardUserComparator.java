package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.UserFriendsDTO;
import java.io.Serializable;
import java.util.Comparator;

public class FriendLeaderboardUserComparator implements Comparator<FriendLeaderboardUserDTO>, Serializable
{
    @Override public int compare(@NonNull FriendLeaderboardUserDTO lhs, @NonNull FriendLeaderboardUserDTO rhs)
    {
        if (lhs instanceof FriendLeaderboardMarkedUserDTO &&
                rhs instanceof FriendLeaderboardSocialUserDTO)
        {
            return -1;
        }

        if (lhs instanceof FriendLeaderboardSocialUserDTO &&
                rhs instanceof FriendLeaderboardMarkedUserDTO)
        {
            return 1;
        }

        if (lhs instanceof FriendLeaderboardSocialUserDTO &&
                rhs instanceof FriendLeaderboardSocialUserDTO)
        {
            UserFriendsDTO lhu = ((FriendLeaderboardSocialUserDTO) lhs).userFriendsDTO;
            UserFriendsDTO rhu = ((FriendLeaderboardSocialUserDTO) rhs).userFriendsDTO;
            if (lhu.equals(rhu))
            {
                return 0;
            }

            return lhu.compareTo(rhu);
        }

        if (lhs instanceof FriendLeaderboardCallToActionUserDTO
                && rhs instanceof FriendLeaderboardCallToActionUserDTO)
        {
            return 0;
        }
        if (lhs instanceof FriendLeaderboardCallToActionUserDTO)
        {
            return 1;
        }
        if (rhs instanceof FriendLeaderboardCallToActionUserDTO)
        {
            return -1;
        }

        if (lhs instanceof FriendLeaderboardMarkedUserDTO &&
                rhs instanceof FriendLeaderboardMarkedUserDTO)
        {
            return Integer.valueOf(lhs.getPosition()).compareTo(rhs.getPosition());
        }

        throw new IllegalArgumentException("Unhandled " + lhs.getClass() + " with " + rhs.getClass());
    }
}
