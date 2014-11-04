package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.notification.DTOProcessorNotificationAllRead;
import com.tradehero.th.models.notification.DTOProcessorNotificationRead;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton
public class NotificationServiceWrapper
{
    @NotNull private final NotificationService notificationService;
    @NotNull private final NotificationServiceAsync notificationServiceAsync;
    @NotNull private final NotificationServiceRx notificationServiceRx;
    @NotNull private final Lazy<NotificationCache> notificationCache;
    @NotNull private final Lazy<UserProfileCacheRx> userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public NotificationServiceWrapper(
            @NotNull NotificationService notificationService,
            @NotNull NotificationServiceAsync notificationServiceAsync,
            @NotNull NotificationServiceRx notificationServiceRx,
            @NotNull Lazy<NotificationCache> notificationCache,
            @NotNull Lazy<UserProfileCacheRx> userProfileCache)
    {
        this.notificationService = notificationService;
        this.notificationServiceAsync = notificationServiceAsync;
        this.notificationServiceRx = notificationServiceRx;
        this.notificationCache = notificationCache;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get Notifications">
    public PaginatedNotificationDTO getNotifications(@NotNull NotificationListKey notificationListKey)
    {
        return notificationService.getNotifications(notificationListKey.toMap());
    }

    public Observable<PaginatedNotificationDTO> getNotificationsRx(@NotNull NotificationListKey notificationListKey)
    {
        return notificationServiceRx.getNotifications(notificationListKey.toMap());
    }
    //</editor-fold>

    //<editor-fold desc="Get Notification Detail">
    public NotificationDTO getNotificationDetail(@NotNull NotificationKey pushKey)
    {
        return notificationService.getNotificationDetail(pushKey.key);
    }

    public Observable<NotificationDTO> getNotificationDetailRx(@NotNull NotificationKey pushKey)
    {
        return notificationServiceRx.getNotificationDetail(pushKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Mark As Read">
    @NotNull private DTOProcessorNotificationRead createNotificationReadDTOProcessor(
            @NotNull final UserBaseKey readerId,
            @NotNull NotificationKey pushKey)
    {
        return new DTOProcessorNotificationRead(
                pushKey,
                notificationCache.get(),
                readerId,
                userProfileCache.get());
    }

    @NotNull public MiddleCallback<BaseResponseDTO> markAsRead(
            @NotNull final UserBaseKey readerId,
            @NotNull NotificationKey pushKey,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        BaseMiddleCallback<BaseResponseDTO> readMiddleCallback = new BaseMiddleCallback<>(callback, createNotificationReadDTOProcessor(readerId, pushKey));
        notificationServiceAsync.markAsRead(pushKey.key, readMiddleCallback);
        return readMiddleCallback;
    }

    public Observable<BaseResponseDTO> markAsReadRx(
            @NotNull final UserBaseKey readerId,
            @NotNull NotificationKey pushKey)
    {
        return notificationServiceRx.markAsRead(pushKey.key)
                .doOnNext(createNotificationReadDTOProcessor(readerId, pushKey));
    }
    //</editor-fold>

    //<editor-fold desc="Mark As Read All">
    @NotNull private DTOProcessorNotificationAllRead createNotificationAllReadDTOProcessor(@NotNull UserBaseKey readerId)
    {
        return new DTOProcessorNotificationAllRead(
                notificationCache.get(),
                readerId,
                userProfileCache.get());
    }

    @NotNull public MiddleCallback<BaseResponseDTO> markAsReadAll(
            @NotNull final UserBaseKey readerId,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        BaseMiddleCallback<BaseResponseDTO> readMiddleCallback = new BaseMiddleCallback<>(callback, createNotificationAllReadDTOProcessor(readerId));
        notificationServiceAsync.markAsReadAll(readMiddleCallback);
        return readMiddleCallback;
    }

    public Observable<BaseResponseDTO> markAsReadAllRx(@NotNull final UserBaseKey readerId)
    {
        return notificationServiceRx.markAsReadAll()
                .doOnNext(createNotificationAllReadDTOProcessor(readerId));
    }
    //</editor-fold>
}
