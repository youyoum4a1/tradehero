package com.ayondo.academy.models.notification;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.notification.NotificationCacheRx;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;

public class DTOProcessorNotificationAllRead extends ThroughDTOProcessor<BaseResponseDTO>
{
    @NonNull private final NotificationCacheRx notificationCache;
    @NonNull private final UserBaseKey readerId;
    @NonNull private final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorNotificationAllRead(
            @NonNull NotificationCacheRx notificationCache,
            @NonNull UserBaseKey readerId,
            @NonNull UserProfileCacheRx userProfileCache)
    {
        this.notificationCache = notificationCache;
        this.readerId = readerId;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        UserProfileDTO userProfileDTO = userProfileCache.getCachedValue(readerId);
        if (userProfileDTO != null)
        {
            userProfileDTO.unreadNotificationsCount = 0;
            userProfileCache.onNext(readerId, userProfileDTO);
        }
        notificationCache.setUnreadAll(false);
        return value;
    }
}
