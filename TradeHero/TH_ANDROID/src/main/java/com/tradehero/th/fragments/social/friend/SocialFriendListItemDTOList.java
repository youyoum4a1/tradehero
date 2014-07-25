package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsDTOList;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SocialFriendListItemDTOList extends ArrayList<SocialFriendListItemDTO>
{
    //<editor-fold desc="Constructors">
    public SocialFriendListItemDTOList()
    {
        super();
    }

    public SocialFriendListItemDTOList(
            @NotNull Collection<? extends UserFriendsDTO> c,
            @Nullable UserFriendsDTO typeQualifier)
    {
        super();
        for (UserFriendsDTO userFriendsDTO : c)
        {
            add(new SocialFriendListItemUserDTO(userFriendsDTO));
        }
    }
    //</editor-fold>

    @NotNull public UserFriendsDTOList getUserFriends()
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
