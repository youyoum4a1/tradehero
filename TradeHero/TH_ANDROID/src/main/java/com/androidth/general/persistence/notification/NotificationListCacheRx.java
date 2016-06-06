package com.androidth.general.persistence.notification;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.common.persistence.prefs.IntPreference;
import com.androidth.general.api.notification.NotificationListKey;
import com.androidth.general.api.notification.PaginatedNotificationDTO;
import com.androidth.general.network.service.NotificationServiceWrapper;
import com.androidth.general.persistence.ListCacheMaxSize;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class NotificationListCacheRx extends BaseFetchDTOCacheRx<NotificationListKey, PaginatedNotificationDTO>
{
    @NonNull private final Lazy<NotificationServiceWrapper> notificationService;
    @NonNull private final Lazy<NotificationCacheRx> notificationCache;

    //<editor-fold desc="Constructors">
    @Inject public NotificationListCacheRx(
            @ListCacheMaxSize IntPreference maxSize,
            @NonNull Lazy<NotificationServiceWrapper> notificationService,
            @NonNull Lazy<NotificationCacheRx> notificationCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize.get(), dtoCacheUtil);
        this.notificationService = notificationService;
        this.notificationCache = notificationCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<PaginatedNotificationDTO> fetch(@NonNull NotificationListKey key)
    {
        return notificationService.get().getNotificationsRx(key);
    }

    @Override public void onNext(@NonNull NotificationListKey key, @NonNull PaginatedNotificationDTO value)
    {
        notificationCache.get().onNext(value.getData());
        super.onNext(key, value);
    }
}
