package com.tradehero.th.models.notification;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

public class DTOProcessorNotificationAllRead implements DTOProcessor<Response>
{
    @NotNull private final NotificationCache notificationCache;
    @NotNull private final UserBaseKey readerId;
    @NotNull private final UserProfileCache userProfileCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorNotificationAllRead(
            @NotNull NotificationCache notificationCache,
            @NotNull UserBaseKey readerId,
            @NotNull UserProfileCache userProfileCache)
    {
        this.notificationCache = notificationCache;
        this.readerId = readerId;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public Response process(Response value)
    {
        UserProfileDTO userProfileDTO = userProfileCache.get(readerId);
        if (userProfileDTO != null)
        {
            userProfileDTO.unreadNotificationsCount = 0;
        }
        userProfileCache.getOrFetchAsync(readerId, true);
        notificationCache.invalidateAll();
        return value;
    }
}
