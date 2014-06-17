package com.tradehero.th.network.service;

import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.notification.DTOProcessorNotificationRead;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.QueryMap;

@Singleton
public class NotificationServiceWrapper
{
    @NotNull private final NotificationService notificationService;
    @NotNull private final NotificationServiceAsync notificationServiceAsync;
    @NotNull private final Lazy<NotificationCache> notificationCache;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final Lazy<UserProfileCache> userProfileCache;

    @Inject public NotificationServiceWrapper(
            @NotNull NotificationService notificationService,
            @NotNull NotificationServiceAsync notificationServiceAsync,
            @NotNull Lazy<NotificationCache> notificationCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull Lazy<UserProfileCache> userProfileCache)
    {
        this.notificationService = notificationService;
        this.notificationServiceAsync = notificationServiceAsync;
        this.notificationCache = notificationCache;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
    }

    @NotNull private DTOProcessor<Response> createNotificationReadDTOProcessor(
            @NotNull NotificationKey pushKey)
    {
        return new DTOProcessorNotificationRead(
                pushKey,
                notificationCache.get(),
                currentUserId,
                userProfileCache.get());
    }

    //<editor-fold desc="Get Notifications">
    public PaginatedDTO<NotificationDTO> getNotifications(@NotNull NotificationListKey notificationListKey)
    {
        return notificationService.getNotifications(notificationListKey.toMap());
    }

    @NotNull public MiddleCallback<PaginatedDTO<NotificationDTO>> getNotifications(
            @NotNull NotificationListKey notificationListKey,
            @Nullable Callback<PaginatedDTO<NotificationDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<NotificationDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        notificationServiceAsync.getNotifications(notificationListKey.toMap(), middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Notification Detail">
    public NotificationDTO getNotificationDetail(@NotNull NotificationKey pushKey)
    {
        return notificationService.getNotificationDetail(pushKey.key);
    }

    @NotNull public MiddleCallback<NotificationDTO> getNotificationDetail(
            @NotNull NotificationKey pushKey,
            @Nullable Callback<NotificationDTO> callback)
    {
        MiddleCallback<NotificationDTO> middleCallback = new BaseMiddleCallback<>(callback);
        notificationServiceAsync.getNotificationDetail(pushKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Mark As Read">
    public Response markAsRead(@NotNull NotificationKey pushKey)
    {
        return createNotificationReadDTOProcessor(pushKey).process(notificationService.markAsRead(pushKey.key));
    }

    @NotNull public MiddleCallback<Response> markAsRead(
            @NotNull NotificationKey pushKey,
            @Nullable Callback<Response> callback)
    {
        BaseMiddleCallback<Response> readMiddleCallback = new BaseMiddleCallback<>(callback, createNotificationReadDTOProcessor(pushKey));
        notificationServiceAsync.markAsRead(pushKey.key, readMiddleCallback);
        return readMiddleCallback;
    }
    //</editor-fold>
}
