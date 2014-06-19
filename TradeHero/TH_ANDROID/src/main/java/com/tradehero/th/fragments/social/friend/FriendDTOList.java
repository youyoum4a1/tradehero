package com.tradehero.th.fragments.social.friend;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import java.util.ArrayList;
import java.util.Collection;

// TODO move to API package
public class FriendDTOList extends ArrayList<UserFriendsDTO>
        implements DTO
{
    //<editor-fold desc="Constructors">
    public FriendDTOList()
    {
        super();
    }

    public FriendDTOList(int capacity)
    {
        super(capacity);
    }

    public FriendDTOList(Collection<? extends UserFriendsDTO> c)
    {
        super(c);
    }
    //</editor-fold>
}
