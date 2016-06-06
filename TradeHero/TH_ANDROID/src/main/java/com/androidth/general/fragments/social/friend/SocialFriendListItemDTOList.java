package com.androidth.general.fragments.social.friend;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.api.social.UserFriendsDTOList;
import java.util.ArrayList;
import java.util.Collection;

public class SocialFriendListItemDTOList extends ArrayList<SocialFriendListItemDTO>
{
    //<editor-fold desc="Constructors">
    public SocialFriendListItemDTOList()
    {
        super();
    }

    public SocialFriendListItemDTOList(
            @NonNull Collection<? extends UserFriendsDTO> c,
            @Nullable UserFriendsDTO typeQualifier)
    {
        super();
        for (UserFriendsDTO userFriendsDTO : c)
        {
            add(new SocialFriendListItemUserDTO(userFriendsDTO));
        }
    }
    //</editor-fold>

    @NonNull public UserFriendsDTOList getUserFriends()
    {
        UserFriendsDTOList found = new UserFriendsDTOList();
        for (SocialFriendListItemDTO item : this)
        {
            if (item instanceof SocialFriendListItemUserDTO)
            {
                found.add(((SocialFriendListItemUserDTO) item).userFriendsDTO);
            }
        }
        return found;
    }
}
