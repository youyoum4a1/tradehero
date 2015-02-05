package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.social.UserFriendsDTO;

public class ProcessableLeaderboardFriendsDTO implements DTO, ContainerDTO<FriendLeaderboardUserDTO, FriendLeaderboardUserDTOList>
{
    @NonNull private final FriendLeaderboardUserDTOFactory factory;
    @NonNull public final LeaderboardFriendsDTO leaderboardFriendsDTO;

    //<editor-fold desc="Constructors">
    public ProcessableLeaderboardFriendsDTO(
            @NonNull FriendLeaderboardUserDTOFactory factory,
            @NonNull LeaderboardFriendsDTO leaderboardFriendsDTO)
    {
        this.factory = factory;
        this.leaderboardFriendsDTO = leaderboardFriendsDTO;
    }
    //</editor-fold>

    @Override public int size()
    {
        return (leaderboardFriendsDTO.leaderboard == null ? 0 : leaderboardFriendsDTO.leaderboard.size())
            + (leaderboardFriendsDTO.socialFriends == null ? 0 : leaderboardFriendsDTO.socialFriends.size());
    }

    @Override public FriendLeaderboardUserDTOList getList()
    {
        FriendLeaderboardUserDTOList list = new FriendLeaderboardUserDTOList();
        if (leaderboardFriendsDTO.leaderboard != null)
        {
            int position = 1;
            FriendLeaderboardUserDTO created;
            for (LeaderboardUserDTO userDTO : leaderboardFriendsDTO.leaderboard.getList())
            {
                created = factory.create(userDTO);
                created.setPosition(position);
                list.add(created);
                position++;
            }
        }
        if (leaderboardFriendsDTO.socialFriends != null)
        {
            for (UserFriendsDTO friend : leaderboardFriendsDTO.socialFriends)
            {
                list.add(factory.create(friend));
            }
        }
        return list;
    }
}
