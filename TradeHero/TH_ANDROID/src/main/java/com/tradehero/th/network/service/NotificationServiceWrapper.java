package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.notification.DTOProcessorNotificationAllRead;
import com.tradehero.th.models.notification.DTOProcessorNotificationRead;
import com.tradehero.th.persistence.notification.NotificationCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
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
    public Observable<PaginatedNotificationDTO> getNotificationsRx(@NonNull NotificationListKey notificationListKey)
    {
        return notificationServiceRx.getNotifications(notificationListKey.toMap());
    }
    //</editor-fold>

    //<editor-fold desc="Get Notification Detail">
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

    public Observable<BaseResponseDTO> markAsReadRx(
            @NonNull final UserBaseKey readerId,
            @NonNull NotificationKey pushKey)
    {
        return notificationServiceRx.markAsRead(pushKey.key)
                .map(createNotificationReadDTOProcessor(readerId, pushKey));
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

    public Observable<BaseResponseDTO> markAsReadAllRx(@NonNull final UserBaseKey readerId)
    {
        return notificationServiceRx.markAsReadAll()
                .map(createNotificationAllReadDTOProcessor(readerId));
    }
    //</editor-fold>
}
