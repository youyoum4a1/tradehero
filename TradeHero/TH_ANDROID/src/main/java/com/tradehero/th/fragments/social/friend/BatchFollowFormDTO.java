package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import java.util.ArrayList;
import java.util.List;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BatchFollowFormDTO
{
    @NonNull public List<Integer> userIds;

    //<editor-fold desc="Constructors">
    public BatchFollowFormDTO()
    {
        super();
        userIds = new ArrayList<>();
    }

    public BatchFollowFormDTO(
            @NonNull List<? extends UserFriendsDTO> userFriendsDTOs,
            @Nullable UserFriendsDTO typeQualifier)
    {
        this();
        for (UserFriendsDTO friendsDTO : userFriendsDTOs)
        {
            userIds.add(friendsDTO.thUserId);
        }
    }

    public BatchFollowFormDTO(
            @NonNull List<? extends UserBaseDTO> userBaseDTOs,
            @Nullable UserBaseDTO typeQualifier)
    {
        this();
        for (UserBaseDTO userBaseDTO : userBaseDTOs)
        {
            userIds.add(userBaseDTO.id);
        }
    }
    //</editor-fold>
}
