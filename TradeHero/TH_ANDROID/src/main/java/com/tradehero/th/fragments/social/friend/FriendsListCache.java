package com.tradehero.th.fragments.social.friend;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

// TODO move to persistence
@Singleton
public class FriendsListCache extends StraightDTOCache<FriendsListKey, UserFriendsDTOList>
{
    @NotNull private final UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public FriendsListCache(
            @NotNull @ListCacheMaxSize IntPreference maxSize,
            @NotNull UserServiceWrapper userServiceWrapper)
    {
        super(maxSize.get());
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    @Override protected UserFriendsDTOList fetch(@NotNull FriendsListKey key) throws Throwable
    {
        return userServiceWrapper.getSocialFriends(key.userBaseKey, key.socialNetworkEnum);
    }
}
