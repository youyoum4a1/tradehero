package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserBaseKeyList;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserBaseKeyListCache extends StraightDTOCacheNew<UserListType, UserBaseKeyList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final Lazy<UserServiceWrapper> userServiceWrapper;
    @NotNull private final Lazy<UserSearchResultCache> userSearchResultCache;

    //<editor-fold desc="Constructors">
    @Inject public UserBaseKeyListCache(
            @NotNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NotNull Lazy<UserSearchResultCache> userSearchResultCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.userServiceWrapper = userServiceWrapper;
        this.userSearchResultCache = userSearchResultCache;
    }
    //</editor-fold>

    @Override @NotNull public UserBaseKeyList fetch(@NotNull UserListType key) throws Throwable
    {
        return putInternal(key, userServiceWrapper.get().searchUsers(key));
    }

    @NotNull protected UserBaseKeyList putInternal(@NotNull UserListType key, @NotNull List<UserSearchResultDTO> fleshedValues)
    {
        UserBaseKeyList userBaseKeys = new UserBaseKeyList();
        UserBaseKey userBaseKey;
        for (@Nullable UserSearchResultDTO userSearchResultDTO: fleshedValues)
        {
            if (userSearchResultDTO != null)
            {
                userBaseKey = userSearchResultDTO.getUserBaseKey();
                userBaseKeys.add(userBaseKey);
                userSearchResultCache.get().put(userBaseKey, userSearchResultDTO);
            }
        }
        put(key, userBaseKeys);
        return userBaseKeys;
    }
}
