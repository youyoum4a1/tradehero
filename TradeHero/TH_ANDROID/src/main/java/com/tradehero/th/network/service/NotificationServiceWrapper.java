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
import com.tradehero.th.persistence.notification.NotificationCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton
public class NotificationServiceWrapper
{
    @NonNull private final NotificationService notificationService;
    @NonNull private final NotificationServiceAsync notificationServiceAsync;
    @NonNull private final NotificationServiceRx notificationServiceRx;
    @NonNull private final Lazy<NotificationCacheRx> notificationCache;
    @NonNull private final Lazy<UserProfileCacheRx> userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public NotificationServiceWrapper(
            @NonNull NotificationService notificationService,
            @NonNull NotificationServiceAsync notificationServiceAsync,
            @NonNull NotificationServiceRx notificationServiceRx,
            @NonNull Lazy<NotificationCacheRx> notificationCache,
            @NonNull Lazy<UserProfileCacheRx> userProfileCache)
    {
        this.notificationService = notificationService;
        this.notificationServiceAsync = notificationServiceAsync;
        this.notificationServiceRx = notificationServiceRx;
        this.notificationCache = notificationCache;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get Notifications">
    public PaginatedNotificationDTO getNotifications(@NonNull NotificationListKey notificationListKey)
    {
        return notificationService.getNotifications(notificationListKey.toMap());
    }

    public Observable<PaginatedNotificationDTO> getNotificationsRx(@NonNull NotificationListKey notificationListKey)
    {
        return notificationServiceRx.getNotifications(notificationListKey.toMap());
    }
    //</editor-fold>

    //<editor-fold desc="Get Notification Detail">
    public NotificationDTO getNotificationDetail(@NonNull NotificationKey pushKey)
    {
        return notificationService.getNotificationDetail(pushKey.key);
    }

    public Observable<NotificationDTO> getNotificationDetailRx(@NonNull NotificationKey pushKey)
    {
        return notificationServiceRx.getNotificationDetail(pushKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Mark As Read">
    @NonNull private DTOProcessorNotificationRead createNotificationReadDTOProcessor(
            @NonNull final UserBaseKey readerId,
            @NonNull NotificationKey pushKey)
    {
        return new DTOProcessorNotificationRead(
                pushKey,
                notificationCache.get(),
                readerId,
                userProfileCache.get());
    }

    @NonNull public MiddleCallback<BaseResponseDTO> markAsRead(
            @NonNull final UserBaseKey readerId,
            @NonNull NotificationKey pushKey,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        BaseMiddleCallback<BaseResponseDTO> readMiddleCallback = new BaseMiddleCallback<>(callback, createNotificationReadDTOProcessor(readerId, pushKey));
        notificationServiceAsync.markAsRead(pushKey.key, readMiddleCallback);
        return readMiddleCallback;
    }

    public Observable<BaseResponseDTO> markAsReadRx(
            @NonNull final UserBaseKey readerId,
            @NonNull NotificationKey pushKey)
    {
        return notificationServiceRx.markAsRead(pushKey.key)
                .doOnNext(createNotificationReadDTOProcessor(readerId, pushKey));
    }
    //</editor-fold>

    //<editor-fold desc="Mark As Read All">
    @NonNull private DTOProcessorNotificationAllRead createNotificationAllReadDTOProcessor(@NonNull UserBaseKey readerId)
    {
        return new DTOProcessorNotificationAllRead(
                notificationCache.get(),
                readerId,
                userProfileCache.get());
    }

    @NonNull public MiddleCallback<BaseResponseDTO> markAsReadAll(
            @NonNull final UserBaseKey readerId,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        BaseMiddleCallback<BaseResponseDTO> readMiddleCallback = new BaseMiddleCallback<>(callback, createNotificationAllReadDTOProcessor(readerId));
        notificationServiceAsync.markAsReadAll(readMiddleCallback);
        return readMiddleCallback;
    }

    public Observable<BaseResponseDTO> markAsReadAllRx(@NonNull final UserBaseKey readerId)
    {
        return notificationServiceRx.markAsReadAll()
                .doOnNext(createNotificationAllReadDTOProcessor(readerId));
    }
    //</editor-fold>
}
