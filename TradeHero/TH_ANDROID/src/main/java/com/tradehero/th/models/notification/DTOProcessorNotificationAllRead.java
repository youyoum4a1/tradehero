package com.tradehero.th.models.notification;

import android.support.annotation.NonNull;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.notification.NotificationCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

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
        UserProfileDTO userProfileDTO = userProfileCache.getValue(readerId);
        if (userProfileDTO != null)
        {
            userProfileDTO.unreadNotificationsCount = 0;
        }
        userProfileCache.get(readerId);
        notificationCache.invalidateAll();
        return value;
    }
}
