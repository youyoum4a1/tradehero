package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdateUserProfile implements DTOProcessor<UserProfileDTO>
{
    @NotNull protected final UserProfileCache userProfileCache;
    @NotNull protected final HomeContentCache homeContentCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorUpdateUserProfile(
            @NotNull UserProfileCache userProfileCache,
            @NotNull HomeContentCache homeContentCache)
    {
        this.userProfileCache = userProfileCache;
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO cached = userProfileCache.get(userProfileDTO.getBaseKey());
        if (cached != null
                && (cached.unreadMessageThreadsCount != userProfileDTO.unreadMessageThreadsCount
                    || cached.unreadNotificationsCount != userProfileDTO.unreadNotificationsCount))
        {
            homeContentCache.invalidate(userProfileDTO.getBaseKey());
        }
        userProfileCache.put(userProfileDTO.getBaseKey(), userProfileDTO);
        return userProfileDTO;
    }
}
