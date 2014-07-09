package com.tradehero.th.persistence.notification;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.notification.NotificationDTOList;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class NotificationListCache extends StraightCutDTOCacheNew<
        NotificationListKey,
        PaginatedNotificationDTO,
        PaginatedNotificationKey>
{
    @NotNull private final Lazy<NotificationServiceWrapper> notificationService;
    @NotNull private final Lazy<NotificationCache> notificationCache;

    //<editor-fold desc="Constructors">
    @Inject public NotificationListCache(
            @ListCacheMaxSize IntPreference maxSize,
            @NotNull Lazy<NotificationServiceWrapper> notificationService,
            @NotNull Lazy<NotificationCache> notificationCache
            )
    {
        super(maxSize.get());
        this.notificationService = notificationService;
        this.notificationCache = notificationCache;
    }
    //</editor-fold>

    @Override @NotNull public PaginatedNotificationDTO fetch(@NotNull NotificationListKey key) throws Throwable
    {
        return notificationService.get().getNotifications(key);
    }

    @NotNull @Override protected PaginatedNotificationKey cutValue(@NotNull NotificationListKey key, @NotNull PaginatedNotificationDTO value)
    {
        notificationCache.get().put(value.getData());
        return new PaginatedNotificationKey(value);
    }

    @Nullable @Override protected PaginatedNotificationDTO inflateValue(@NotNull NotificationListKey key, @Nullable PaginatedNotificationKey cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        NotificationDTOList value = notificationCache.get().get(cutValue.getData());
        if (value.hasNullItem())
        {
            return null;
        }
        return new PaginatedNotificationDTO(cutValue.getPagination(), value);
    }
}
