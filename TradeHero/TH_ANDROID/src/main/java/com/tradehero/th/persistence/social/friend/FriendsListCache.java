package com.tradehero.th.persistence.social.friend;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.th.api.social.key.FriendsListKey;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class FriendsListCache extends StraightDTOCacheNew<FriendsListKey, UserFriendsDTOList>
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

    @Override @NotNull public UserFriendsDTOList fetch(@NotNull FriendsListKey key) throws Throwable
    {
        return userServiceWrapper.getSocialFriends(key.userBaseKey, key.socialNetworkEnum);
    }
}
