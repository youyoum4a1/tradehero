package com.androidth.general.models.notification;

import android.support.annotation.NonNull;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.persistence.notification.NotificationCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;

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
