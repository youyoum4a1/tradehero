package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.notification.NotificationDTO;
import com.ayondo.academy.api.notification.NotificationKey;
import com.ayondo.academy.api.notification.NotificationListKey;
import com.ayondo.academy.api.notification.PaginatedNotificationDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.models.notification.DTOProcessorNotificationAllRead;
import com.ayondo.academy.models.notification.DTOProcessorNotificationRead;
import com.ayondo.academy.persistence.notification.NotificationCacheRx;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton
public class NotificationServiceWrapper
{
    @NonNull private final NotificationServiceRx notificationServiceRx;
    @NonNull private final Lazy<NotificationCacheRx> notificationCache;
    @NonNull private final Lazy<UserProfileCacheRx> userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public NotificationServiceWrapper(
            @NonNull NotificationServiceRx notificationServiceRx,
            @NonNull Lazy<NotificationCacheRx> notificationCache,
            @NonNull Lazy<UserProfileCacheRx> userProfileCache)
    {
        this.notificationServiceRx = notificationServiceRx;
        this.notificationCache = notificationCache;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get Notifications">
    @NonNull public Observable<PaginatedNotificationDTO> getNotificationsRx(@NonNull NotificationListKey notificationListKey)
    {
        return notificationServiceRx.getNotifications(notificationListKey.toMap());
    }
    //</editor-fold>

    //<editor-fold desc="Get Notification Detail">
    @NonNull public Observable<NotificationDTO> getNotificationDetailRx(@NonNull NotificationKey pushKey)
    {
        return notificationServiceRx.getNotificationDetail(pushKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Mark As Read">
    @NonNull public Observable<BaseResponseDTO> markAsReadRx(
            @NonNull final UserBaseKey readerId,
            @NonNull NotificationKey pushKey)
    {
        return notificationServiceRx.markAsRead(pushKey.key)
                .map(new DTOProcessorNotificationRead(
                        pushKey,
                        notificationCache.get(),
                        readerId,
                        userProfileCache.get()));
    }
    //</editor-fold>

    //<editor-fold desc="Mark As Read All">
    @NonNull public Observable<BaseResponseDTO> markAsReadAllRx(@NonNull final UserBaseKey readerId)
    {
        return notificationServiceRx.markAsReadAll()
                .map(new DTOProcessorNotificationAllRead(
                        notificationCache.get(),
                        readerId,
                        userProfileCache.get()));
    }
    //</editor-fold>
}
