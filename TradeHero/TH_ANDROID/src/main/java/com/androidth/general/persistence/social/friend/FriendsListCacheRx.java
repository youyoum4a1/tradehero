package com.androidth.general.persistence.social.friend;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.common.persistence.prefs.IntPreference;
import com.androidth.general.api.social.UserFriendsDTOList;
import com.androidth.general.api.social.key.FriendsListKey;
import com.androidth.general.network.service.UserServiceWrapper;
import com.androidth.general.persistence.ListCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class FriendsListCacheRx extends BaseFetchDTOCacheRx<FriendsListKey, UserFriendsDTOList>
{
    @NonNull private final UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public FriendsListCacheRx(
            @NonNull @ListCacheMaxSize IntPreference maxSize,
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize.get(), dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<UserFriendsDTOList> fetch(@NonNull FriendsListKey key)
    {
        return userServiceWrapper.getFriendsRx(key);
    }
}
