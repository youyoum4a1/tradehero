package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import java.util.Comparator;
import android.support.annotation.NonNull;

public class FriendLeaderboardUserComparator implements Comparator<FriendLeaderboardUserDTO>
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

        if (lhs instanceof FriendLeaderboardMarkedUserDTO &&
                rhs instanceof FriendLeaderboardMarkedUserDTO)
        {
            LeaderboardUserDTO lhl = ((FriendLeaderboardMarkedUserDTO) lhs).leaderboardUserDTO;
            LeaderboardUserDTO rhl = ((FriendLeaderboardMarkedUserDTO) rhs).leaderboardUserDTO;

            return lhl.getPosition().compareTo(rhl.getPosition());
        }

        throw new IllegalArgumentException("Unhandled " + lhs.getClass() + " with " + rhs.getClass());
    }
}
