package com.tradehero.th.models.notification;

import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

public class DTOProcessorNotificationRead implements DTOProcessor<Response>
{
    @NotNull private final NotificationKey key;
    @NotNull private final NotificationCache notificationCache;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final UserProfileCache userProfileCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorNotificationRead(
            @NotNull NotificationKey key,
            @NotNull NotificationCache notificationCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache)
    {
        this.key = key;
        this.notificationCache = notificationCache;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public Response process(Response value)
    {
        NotificationDTO notificationDTO = notificationCache.get(key);
        if (notificationDTO != null)
        {
            notificationDTO.unread = false;
        }
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null && userProfileDTO.unreadNotificationsCount > 0)
        {
            userProfileDTO.unreadNotificationsCount--;
        }

        return value;
    }
}
