package com.ayondo.academy.persistence.user;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.users.UserListType;
import com.ayondo.academy.api.users.UserSearchResultDTOList;
import com.ayondo.academy.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class UserBaseKeyListCacheRx extends BaseFetchDTOCacheRx<UserListType, UserSearchResultDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final Lazy<UserServiceWrapper> userServiceWrapper;
    @NonNull private final Lazy<UserSearchResultCacheRx> userSearchResultCache;

    //<editor-fold desc="Constructors">
    @Inject public UserBaseKeyListCacheRx(
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<UserSearchResultCacheRx> userSearchResultCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.userSearchResultCache = userSearchResultCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<UserSearchResultDTOList> fetch(@NonNull UserListType key)
    {
        return userServiceWrapper.get().searchUsersRx(key);
    }

    @Override public void onNext(@NonNull UserListType key, @NonNull UserSearchResultDTOList value)
    {
        userSearchResultCache.get().onNext(value);
        super.onNext(key, value);
    }
}
