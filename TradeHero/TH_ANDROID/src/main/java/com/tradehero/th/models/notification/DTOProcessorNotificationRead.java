package com.tradehero.th.models.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

public class DTOProcessorNotificationRead implements DTOProcessor<Response>
{
    @NotNull private final NotificationKey key;
    @NotNull private final Context context;
    @NotNull private final NotificationCache notificationCache;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final UserProfileCache userProfileCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorNotificationRead(
            @NotNull NotificationKey key,
            @NotNull Context context,
            @NotNull NotificationCache notificationCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache)
    {
        this.key = key;
        this.context = context;
        this.notificationCache = notificationCache;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public Response process(Response value)
    {
        NotificationDTO notificationDTO = notificationCache.get(key);
        boolean previousUnread = false;
        if (notificationDTO != null)
        {
            previousUnread = notificationDTO.unread;
            notificationDTO.unread = false;
        }
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (previousUnread && userProfileDTO != null && userProfileDTO.unreadNotificationsCount > 0)
        {
            userProfileDTO.unreadNotificationsCount--;
        }
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey(), true);

        if (previousUnread)
        {
            Intent requestUpdateIntent = new Intent(UpdateCenterFragment.REQUEST_UPDATE_UNREAD_COUNTER);
            LocalBroadcastManager.getInstance(context).sendBroadcast(requestUpdateIntent);
        }

        return value;
    }
}
