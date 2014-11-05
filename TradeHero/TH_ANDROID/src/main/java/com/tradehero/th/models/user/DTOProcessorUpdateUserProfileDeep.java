package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import android.support.annotation.NonNull;

public class DTOProcessorUpdateUserProfileDeep extends DTOProcessorUpdateUserProfile
{
    //<editor-fold desc="Constructors">
    public DTOProcessorUpdateUserProfileDeep(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache)
    {
        super(userProfileCache, homeContentCache);
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        homeContentCache.invalidate(userProfileDTO.getBaseKey());
        return processed;
    }
}
