package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdateUserProfile implements DTOProcessor<UserProfileDTO>
{
    @NotNull protected final UserProfileCache userProfileCache;

    public DTOProcessorUpdateUserProfile(@NotNull UserProfileCache userProfileCache)
    {
        this.userProfileCache = userProfileCache;
    }

    @Override public UserProfileDTO process(UserProfileDTO userProfileDTO)
    {
        userProfileCache.put(userProfileDTO.getBaseKey(), userProfileDTO);
        return userProfileDTO;
    }
}
