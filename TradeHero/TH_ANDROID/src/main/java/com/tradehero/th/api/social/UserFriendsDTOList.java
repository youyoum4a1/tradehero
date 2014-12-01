package com.tradehero.th.api.social;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseArrayListHasExpiration;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.utils.CollectionUtils;
import java.util.Collection;
import java.util.List;

public class UserFriendsDTOList extends BaseArrayListHasExpiration<UserFriendsDTO>
        implements DTO
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 60;

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

    @NonNull public List<UserFriendsDTO> getTradeHeroUsers()
    {
        return CollectionUtils.filter(this, UserFriendsDTO::isTradeHeroUser);
    }

    @NonNull public List<UserFriendsDTO> getNonTradeHeroUsers()
    {
        return CollectionUtils.filter(this, UserFriendsDTO::isNonTradeHeroUser);
    }
}
