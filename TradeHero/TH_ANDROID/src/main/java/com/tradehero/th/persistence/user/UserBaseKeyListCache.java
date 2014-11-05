package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserBaseKeyList;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTOList;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Singleton @UserCache @Deprecated
public class UserBaseKeyListCache extends StraightCutDTOCacheNew<UserListType, UserSearchResultDTOList, UserBaseKeyList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NonNull private final Lazy<UserServiceWrapper> userServiceWrapper;
    @NonNull private final Lazy<UserSearchResultCacheRx> userSearchResultCache;

    //<editor-fold desc="Constructors">
    @Inject public UserBaseKeyListCache(
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<UserSearchResultCacheRx> userSearchResultCache,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.userSearchResultCache = userSearchResultCache;
    }
    //</editor-fold>

    @Override @NonNull public UserSearchResultDTOList fetch(@NonNull UserListType key) throws Throwable
    {
        return userServiceWrapper.get().searchUsers(key);
    }

    @NonNull @Override protected UserBaseKeyList cutValue(@NonNull UserListType key, @NonNull UserSearchResultDTOList value)
    {
        userSearchResultCache.get().onNext(value);
        return value.createKeys();
    }

    @Nullable @Override protected UserSearchResultDTOList inflateValue(@NonNull UserListType key, @Nullable UserBaseKeyList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        UserSearchResultDTOList list = new UserSearchResultDTOList();
        for (UserBaseKey key1 : cutValue)
        {
            list.add(userSearchResultCache.get().getValue(key1));
        }
        if (list.hasNullItem())
        {
            return null;
        }
        return list;
    }
}
