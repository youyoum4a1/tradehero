package com.tradehero.th.network.service;

import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.notification.DTOProcessorNotificationAllRead;
import com.tradehero.th.models.notification.DTOProcessorNotificationRead;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationServiceWrapper
{
    @NotNull private final NotificationService notificationService;
    @NotNull private final NotificationServiceAsync notificationServiceAsync;
    @NotNull private final Lazy<NotificationCache> notificationCache;
    @NotNull private final Lazy<UserProfileCache> userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public NotificationServiceWrapper(
            @NotNull NotificationService notificationService,
            @NotNull NotificationServiceAsync notificationServiceAsync,
            @NotNull Lazy<NotificationCache> notificationCache,
            @NotNull Lazy<UserProfileCache> userProfileCache)
    {
        this.notificationService = notificationService;
        this.notificationServiceAsync = notificationServiceAsync;
        this.notificationCache = notificationCache;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get Notifications">
    public PaginatedNotificationDTO getNotifications(@NotNull NotificationListKey notificationListKey)
    {
        return notificationService.getNotifications(notificationListKey.toMap());
    }

    //<editor-fold desc="Get Notification Detail">
    public NotificationDTO getNotificationDetail(@NotNull NotificationKey pushKey)
    {
        return notificationService.getNotificationDetail(pushKey.key);
    }

    //<editor-fold desc="Mark As Read">
    @NotNull private DTOProcessor<Response> createNotificationReadDTOProcessor(
            @NotNull final UserBaseKey readerId,
            @NotNull NotificationKey pushKey)
    {
        return new DTOProcessorNotificationRead(
                pushKey,
                notificationCache.get(),
                readerId,
                userProfileCache.get());
    }

    @NotNull public MiddleCallback<Response> markAsRead(
            @NotNull final UserBaseKey readerId,
            @NotNull NotificationKey pushKey,
            @Nullable Callback<Response> callback)
    {
        BaseMiddleCallback<Response> readMiddleCallback = new BaseMiddleCallback<>(callback, createNotificationReadDTOProcessor(readerId, pushKey));
        notificationServiceAsync.markAsRead(pushKey.key, readMiddleCallback);
        return readMiddleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Mark As Read All">
    @NotNull private DTOProcessor<Response> createNotificationAllReadDTOProcessor(@NotNull UserBaseKey readerId)
    {
        return new DTOProcessorNotificationAllRead(
                notificationCache.get(),
                readerId,
                userProfileCache.get());
    }

    @NotNull public MiddleCallback<Response> markAsReadAll(
            @NotNull final UserBaseKey readerId,
            @Nullable Callback<Response> callback)
    {
        BaseMiddleCallback<Response> readMiddleCallback = new BaseMiddleCallback<>(callback, createNotificationAllReadDTOProcessor(readerId));
        notificationServiceAsync.markAsReadAll(readMiddleCallback);
        return readMiddleCallback;
    }
    //</editor-fold>

    public void deleteNotification(int pushId, Callback<String> callback){
        notificationServiceAsync.deleteNotification(pushId, callback);
    }

    public void deleteAllNotification(Callback<String> callback){
        notificationServiceAsync.deleteAllNotifications(callback);
    }
}
