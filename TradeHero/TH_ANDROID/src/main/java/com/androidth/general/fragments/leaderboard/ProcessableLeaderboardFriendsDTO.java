package com.androidth.general.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.api.leaderboard.position.LeaderboardFriendsDTO;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.api.users.UserProfileDTO;

public class ProcessableLeaderboardFriendsDTO
        implements DTO, ContainerDTO<LeaderboardItemDisplayDTO, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>
{
    @NonNull private final LeaderboardItemDisplayDTO.Factory factory;
    @NonNull public final LeaderboardFriendsDTO leaderboardFriendsDTO;
    @NonNull private final UserProfileDTO currentUserProfile;

    //<editor-fold desc="Constructors">
    public ProcessableLeaderboardFriendsDTO(
            @NonNull LeaderboardItemDisplayDTO.Factory factory,
            @NonNull LeaderboardFriendsDTO leaderboardFriendsDTO,
            @NonNull UserProfileDTO currentUserProfile)
    {
        this.factory = factory;
        this.leaderboardFriendsDTO = leaderboardFriendsDTO;
        this.currentUserProfile = currentUserProfile;
    }
    //</editor-fold>

    @Override public int size()
    {
        return (leaderboardFriendsDTO.leaderboard == null ? 0 : leaderboardFriendsDTO.leaderboard.size())
            + (leaderboardFriendsDTO.socialFriends == null ? 0 : leaderboardFriendsDTO.socialFriends.size());
    }

    @Override public LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO> getList()
    {
        boolean containsCurrentUser = false;
        LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO> list =
                new LeaderboardItemDisplayDTO.DTOList<>(leaderboardFriendsDTO.leaderboard);
        if (leaderboardFriendsDTO.leaderboard != null)
        {
            int position = 1;
            LeaderboardItemDisplayDTO created;
            for (LeaderboardUserDTO userDTO : leaderboardFriendsDTO.leaderboard.getList())
            {
                if (userDTO.id == currentUserProfile.id)
                {
                    containsCurrentUser = true;
                }
                created = factory.create(userDTO);
                created.setRanking(position);
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

        if (list.size() <= (containsCurrentUser ? 1 : 0))
        {
            list.add(new FriendLeaderboardItemDisplayDTO.CallToAction(this.factory.resources));
        }

        return list;
    }
}
