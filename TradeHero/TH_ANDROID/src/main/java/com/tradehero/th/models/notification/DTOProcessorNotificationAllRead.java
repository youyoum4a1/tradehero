package com.tradehero.th.models.notification;

import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

public class DTOProcessorNotificationAllRead implements DTOProcessor<Response>
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final UserProfileCache userProfileCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorNotificationAllRead(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache)
    {
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public Response process(Response value)
    {
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            userProfileDTO.unreadNotificationsCount = 0;
        }
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey(), true);
        return value;
    }
}
