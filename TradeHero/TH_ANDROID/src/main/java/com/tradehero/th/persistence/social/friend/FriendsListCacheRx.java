package com.tradehero.th.persistence.social.friend;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.th.api.social.key.FriendsListKey;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class FriendsListCacheRx extends BaseFetchDTOCacheRx<FriendsListKey, UserFriendsDTOList>
{
    @NotNull private final UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public FriendsListCacheRx(
            @NotNull @ListCacheMaxSize IntPreference maxSize,
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize.get(), 5, 5, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<UserFriendsDTOList> fetch(@NotNull FriendsListKey key)
    {
        return userServiceWrapper.getFriendsRx(key);
    }
}
