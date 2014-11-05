package com.tradehero.th.models.notification;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.notification.NotificationCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import android.support.annotation.NonNull;
import rx.functions.Action1;

public class DTOProcessorNotificationRead implements DTOProcessor<BaseResponseDTO>,
        Action1<BaseResponseDTO>
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
        NotificationDTO notificationDTO = notificationCache.getValue(key);
        boolean previousUnread = false;
        if (notificationDTO != null)
        {
            previousUnread = notificationDTO.unread;
            notificationDTO.unread = false;
        }
        UserProfileDTO userProfileDTO = userProfileCache.getValue(readerId);
        if (previousUnread && userProfileDTO != null && userProfileDTO.unreadNotificationsCount > 0)
        {
            userProfileDTO.unreadNotificationsCount--;
        }
        userProfileCache.get(readerId);
        return value;
    }

    @Override public void call(BaseResponseDTO baseResponseDTO)
    {
        process(baseResponseDTO);
    }
}
