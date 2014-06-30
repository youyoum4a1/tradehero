package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class UserProfileCompactCache extends StraightDTOCacheNew<UserBaseKey, UserProfileCompactDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final UserProfileCache userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileCompactCache(@NotNull UserProfileCache userProfileCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override @NotNull public UserProfileCompactDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return userProfileCache.getOrFetchSync(key);
    }
}
