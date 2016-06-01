package com.ayondo.academy.models.user;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;

public class DTOProcessorUpdateUserProfile extends ThroughDTOProcessor<UserProfileDTO>
{
    @NonNull protected final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public DTOProcessorUpdateUserProfile(
            @NonNull UserProfileCacheRx userProfileCache)
    {
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        userProfileCache.onNext(userProfileDTO.getBaseKey(), userProfileDTO);
        return userProfileDTO;
    }
}
