package com.tradehero.th.models.user;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
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
