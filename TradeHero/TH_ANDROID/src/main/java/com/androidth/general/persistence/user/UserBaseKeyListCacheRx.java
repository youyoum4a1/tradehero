package com.androidth.general.persistence.user;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.users.UserListType;
import com.androidth.general.api.users.UserSearchResultDTOList;
import com.androidth.general.network.service.UserServiceWrapper;
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
