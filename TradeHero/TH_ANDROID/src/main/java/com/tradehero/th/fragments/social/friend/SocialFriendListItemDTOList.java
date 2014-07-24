package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.UserFriendsDTO;
import java.util.ArrayList;
import java.util.Collection;

public class SocialFriendListItemDTOList extends ArrayList<SocialFriendListItemDTO>
{
    //<editor-fold desc="Constructors">
    public SocialFriendListItemDTOList()
    {
        super();
    }

    public SocialFriendListItemDTOList(Collection<? extends UserFriendsDTO> c, UserFriendsDTO typeQualifier)
    {
        super();
        for (UserFriendsDTO userFriendsDTO : c)
        {
            add(new SocialFriendListItemUserDTO(userFriendsDTO));
        }
    }
    //</editor-fold>
}
