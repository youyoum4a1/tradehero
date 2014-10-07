package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdateUserProfileDeep extends DTOProcessorUpdateUserProfile
{
    @NotNull private final HomeContentCache homeContentCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorUpdateUserProfileDeep(
            @NotNull UserProfileCache userProfileCache,
            @NotNull HomeContentCache homeContentCache)
    {
        super(userProfileCache);
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        homeContentCache.invalidate(userProfileDTO.getBaseKey());
        return processed;
    }
}
