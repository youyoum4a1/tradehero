package com.androidth.general.api.social;

import android.support.annotation.NonNull;
import com.android.internal.util.Predicate;
import com.androidth.general.common.persistence.BaseArrayListHasExpiration;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.common.utils.CollectionUtils;
import com.androidth.general.api.social.key.FriendKey;
import java.util.Collection;
import java.util.List;
import rx.functions.Func1;

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
        return CollectionUtils.filter(this, new Predicate<UserFriendsDTO>()
        {
            @Override public boolean apply(UserFriendsDTO friends)
            {
                return friends.isTradeHeroUser();
            }
        });
    }

    @NonNull public List<UserFriendsDTO> getNonTradeHeroUsers()
    {
        return CollectionUtils.filter(this, new Predicate<UserFriendsDTO>()
        {
            @Override public boolean apply(UserFriendsDTO friends)
            {
                return friends.isNonTradeHeroUser();
            }
        });
    }

    @NonNull public List<FriendKey> getFriendKeys()
    {
        return CollectionUtils.map(this, new Func1<UserFriendsDTO, FriendKey>()
        {
            @Override public FriendKey call(UserFriendsDTO userFriendsDTO)
            {
                return userFriendsDTO.getFriendKey();
            }
        });
    }
}
