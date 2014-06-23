package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserProfileCompactCache extends StraightDTOCache<UserBaseKey, UserProfileCompactDTO>
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

    @Override protected UserProfileCompactDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return userProfileCache.fetch(key);
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<UserProfileCompactDTO> get(@Nullable List<UserBaseKey> baseKeys)
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserProfileCompactDTO> userProfileDTOs = new ArrayList<>();
        for (@NotNull UserBaseKey baseKey: baseKeys)
        {
            userProfileDTOs.add(get(baseKey));
        }
        return userProfileDTOs;
    }
}
