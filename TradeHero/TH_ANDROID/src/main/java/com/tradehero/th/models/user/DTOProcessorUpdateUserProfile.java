package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;

public class DTOProcessorUpdateUserProfile implements DTOProcessor<UserProfileDTO>
{
    protected final UserProfileCache userProfileCache;

    public DTOProcessorUpdateUserProfile(UserProfileCache userProfileCache)
    {
        this.userProfileCache = userProfileCache;
    }

    @Override public UserProfileDTO process(UserProfileDTO userProfileDTO)
    {
        userProfileCache.put(userProfileDTO.getBaseKey(), userProfileDTO);
        return userProfileDTO;
    }
}
