package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserProfileCompactCache extends StraightDTOCache<UserBaseKey, UserProfileCompactDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject UserProfileCache userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileCompactCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserProfileCompactDTO fetch(UserBaseKey key) throws Throwable
    {
        return userProfileCache.fetch(key);
    }

    public List<UserProfileCompactDTO> get(List<UserBaseKey> baseKeys)
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserProfileCompactDTO> userProfileDTOs = new ArrayList<>();
        for (UserBaseKey baseKey: baseKeys)
        {
            userProfileDTOs.add(get(baseKey));
        }
        return userProfileDTOs;
    }
}
