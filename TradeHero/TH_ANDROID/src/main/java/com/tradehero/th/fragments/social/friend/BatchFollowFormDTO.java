package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.UserFriendsDTO;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class BatchFollowFormDTO
{
    @NotNull public List<Integer> userIds;

    //<editor-fold desc="Constructors">
    public BatchFollowFormDTO()
    {
        super();
        userIds = new ArrayList<>();
    }

    public BatchFollowFormDTO(@NotNull List<? extends UserFriendsDTO> userFriendsDTOs)
    {
        this();
        for (UserFriendsDTO friendsDTO : userFriendsDTOs)
        {
            userIds.add(friendsDTO.thUserId);
        }
    }
    //</editor-fold>
}
