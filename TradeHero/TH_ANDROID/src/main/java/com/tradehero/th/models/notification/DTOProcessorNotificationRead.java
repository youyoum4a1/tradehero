package com.ayondo.academy.models.notification;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.notification.NotificationDTO;
import com.ayondo.academy.api.notification.NotificationKey;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.notification.NotificationCacheRx;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;

public class DTOProcessorNotificationRead extends ThroughDTOProcessor<BaseResponseDTO>
{
    @NonNull private final NotificationKey key;
    @NonNull private final NotificationCacheRx notificationCache;
    @NonNull private final UserBaseKey readerId;
    @NonNull private final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorNotificationRead(
            @NonNull NotificationKey key,
            @NonNull NotificationCacheRx notificationCache,
            @NonNull UserBaseKey readerId,
            @NonNull UserProfileCacheRx userProfileCache)
    {
        this.key = key;
        this.notificationCache = notificationCache;
        this.readerId = readerId;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        NotificationDTO notificationDTO = notificationCache.getCachedValue(key);
        boolean previousUnread = false;
        if (notificationDTO != null)
        {
            previousUnread = notificationDTO.unread;
            notificationDTO.unread = false;
            notificationCache.onNext(key, notificationDTO);
        }
        UserProfileDTO userProfileDTO = userProfileCache.getCachedValue(readerId);
        if (previousUnread && userProfileDTO != null && userProfileDTO.unreadNotificationsCount > 0)
        {
            userProfileDTO.unreadNotificationsCount--;
            userProfileCache.onNext(readerId, userProfileDTO);
        }
        return value;
    }
}
