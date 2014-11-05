package com.tradehero.th.persistence.notification;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
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
        super(maxSize.get(), 5, 5, dtoCacheUtil);
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
