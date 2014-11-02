package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdateUserProfileDeep extends DTOProcessorUpdateUserProfile
{
    //<editor-fold desc="Constructors">
    public DTOProcessorUpdateUserProfileDeep(
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache)
    {
        super(userProfileCache, homeContentCache);
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        homeContentCache.invalidate(userProfileDTO.getBaseKey());
        return processed;
    }
}
