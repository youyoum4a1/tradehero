package com.tradehero.th.models.notification;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;
import rx.functions.Action1;

public class DTOProcessorNotificationRead implements DTOProcessor<BaseResponseDTO>,
        Action1<BaseResponseDTO>
{
    @NotNull private final NotificationKey key;
    @NotNull private final NotificationCache notificationCache;
    @NotNull private final UserBaseKey readerId;
    @NotNull private final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorNotificationRead(
            @NotNull NotificationKey key,
            @NotNull NotificationCache notificationCache,
            @NotNull UserBaseKey readerId,
            @NotNull UserProfileCacheRx userProfileCache)
    {
        this.key = key;
        this.notificationCache = notificationCache;
        this.readerId = readerId;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        NotificationDTO notificationDTO = notificationCache.get(key);
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
