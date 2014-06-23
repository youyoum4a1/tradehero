package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

public class UserFriendsDTOList extends ArrayList<UserFriendsDTO>
        implements DTO
{
    //<editor-fold desc="Constructors">
    public UserFriendsDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public UserFriendsDTOList()
    {
        super();
    }

    public UserFriendsDTOList(Collection<? extends UserFriendsDTO> c)
    {
        super(c);
    }
    //</editor-fold>
}
