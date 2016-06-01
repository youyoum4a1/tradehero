package com.ayondo.academy.api.social;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.users.UserBaseDTO;
import com.ayondo.academy.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class BatchFollowFormDTO implements DTO
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
            @SuppressWarnings("UnusedParameters") @Nullable UserFriendsDTO typeQualifier)
    {
        this();
        for (UserFriendsDTO friendsDTO : userFriendsDTOs)
        {
            userIds.add(friendsDTO.thUserId);
        }
    }

    public BatchFollowFormDTO(
            @NonNull List<? extends UserBaseDTO> userBaseDTOs,
            @SuppressWarnings("UnusedParameters") @Nullable UserBaseDTO typeQualifier)
    {
        this();
        for (UserBaseDTO userBaseDTO : userBaseDTOs)
        {
            userIds.add(userBaseDTO.id);
        }
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "BatchFollowFormDTO{" +
                "userIds=[" + StringUtils.join(",", userIds) + "]" +
                '}';
    }
}
