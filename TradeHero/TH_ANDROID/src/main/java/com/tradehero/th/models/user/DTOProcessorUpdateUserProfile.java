package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import rx.functions.Action1;

public class DTOProcessorUpdateUserProfile implements DTOProcessor<UserProfileDTO>
    , Action1<UserProfileDTO>
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
        UserProfileDTO cached = userProfileCache.getValue(userProfileDTO.getBaseKey());
        if (cached != null
                && (cached.unreadMessageThreadsCount != userProfileDTO.unreadMessageThreadsCount
                    || cached.unreadNotificationsCount != userProfileDTO.unreadNotificationsCount))
        {
            homeContentCache.invalidate(userProfileDTO.getBaseKey());
        }
        userProfileCache.onNext(userProfileDTO.getBaseKey(), userProfileDTO);
        return userProfileDTO;
    }

    @Override public void call(@NonNull UserProfileDTO userProfileDTO)
    {
        process(userProfileDTO);
    }
}
