package com.tradehero.th.models.notification;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;
import rx.functions.Action1;

public class DTOProcessorNotificationAllRead implements DTOProcessor<BaseResponseDTO>,
        Action1<BaseResponseDTO>
{
    @NotNull private final NotificationCache notificationCache;
    @NotNull private final UserBaseKey readerId;
    @NotNull private final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorNotificationAllRead(
            @NotNull NotificationCache notificationCache,
            @NotNull UserBaseKey readerId,
            @NotNull UserProfileCacheRx userProfileCache)
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

    @Override public void call(BaseResponseDTO baseResponseDTO)
    {
        process(baseResponseDTO);
    }
}
