package com.tradehero.th.api.social;

import com.tradehero.common.persistence.BaseArrayListHasExpiration;
import com.tradehero.common.persistence.DTO;
import java.util.Collection;

public class UserFriendsDTOList extends BaseArrayListHasExpiration<UserFriendsDTO>
        implements DTO
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 20;

    //<editor-fold desc="Constructors">
    public UserFriendsDTOList()
    {
        super(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }

    public UserFriendsDTOList(Collection<? extends UserFriendsDTO> c)
    {
        super(c, DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }
    //</editor-fold>
}
