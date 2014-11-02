package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.functions.Action1;

public class DTOProcessorUpdateUserProfile implements DTOProcessor<UserProfileDTO>
    , Action1<UserProfileDTO>
{
    @NotNull protected final UserProfileCacheRx userProfileCache;
    @NotNull protected final HomeContentCacheRx homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public DTOProcessorUpdateUserProfile(
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache)
    {
        this.userProfileCache = userProfileCache;
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
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

    @Override public void call(@NotNull UserProfileDTO userProfileDTO)
    {
        process(userProfileDTO);
    }
}
