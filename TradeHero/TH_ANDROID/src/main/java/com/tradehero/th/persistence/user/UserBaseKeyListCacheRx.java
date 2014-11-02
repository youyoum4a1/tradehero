package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTOList;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class UserBaseKeyListCacheRx extends BaseFetchDTOCacheRx<UserListType, UserSearchResultDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NotNull private final Lazy<UserServiceWrapper> userServiceWrapper;
    @NotNull private final Lazy<UserSearchResultCacheRx> userSearchResultCache;

    //<editor-fold desc="Constructors">
    @Inject public UserBaseKeyListCacheRx(
            @NotNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NotNull Lazy<UserSearchResultCacheRx> userSearchResultCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.userSearchResultCache = userSearchResultCache;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<UserSearchResultDTOList> fetch(@NotNull UserListType key)
    {
        return userServiceWrapper.get().searchUsersRx(key);
    }

    @Override public void onNext(@NotNull UserListType key, @NotNull UserSearchResultDTOList value)
    {
        userSearchResultCache.get().onNext(value);
        super.onNext(key, value);
    }
}
