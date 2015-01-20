package com.tradehero.th.models.user;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;

public class DTOProcessorUpdateUserProfile extends ThroughDTOProcessor<UserProfileDTO>
{
    @NonNull protected final UserProfileCacheRx userProfileCache;
    @NonNull protected final HomeContentCacheRx homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public DTOProcessorUpdateUserProfile(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache)
    {
        this.userProfileCache = userProfileCache;
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO cached = userProfileCache.getCachedValue(userProfileDTO.getBaseKey());
        if (cached != null
                && (cached.unreadMessageThreadsCount != userProfileDTO.unreadMessageThreadsCount
                    || cached.unreadNotificationsCount != userProfileDTO.unreadNotificationsCount))
        {
            homeContentCache.invalidate(userProfileDTO.getBaseKey());
        }
        userProfileCache.onNext(userProfileDTO.getBaseKey(), userProfileDTO);
        return userProfileDTO;
    }
}
