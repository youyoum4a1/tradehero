package com.tradehero.th.persistence.notification;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class NotificationCache extends StraightDTOCacheNew<NotificationKey, NotificationDTO>
{
    @NotNull private final Lazy<NotificationServiceWrapper> notificationServiceWrapper;

    @Inject public NotificationCache(
            @SingleCacheMaxSize IntPreference maxSize,
            @NotNull Lazy<NotificationServiceWrapper> notificationServiceWrapper)
    {
        super(maxSize.get());

        this.notificationServiceWrapper = notificationServiceWrapper;
    }

    @Override @NotNull public NotificationDTO fetch(@NotNull NotificationKey key) throws Throwable
    {
        return notificationServiceWrapper.get().getNotificationDetail(key);
    }
}
