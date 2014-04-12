package com.tradehero.th.persistence.notification;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.network.service.NotificationService;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thonguyen on 3/4/14.
 */
@Singleton
public class NotificationCache extends StraightDTOCache<NotificationKey, NotificationDTO>
{
    private final Lazy<NotificationService> notificationService;

    @Inject public NotificationCache(@SingleCacheMaxSize IntPreference maxSize, Lazy<NotificationService> notificationService)
    {
        super(maxSize.get());

        this.notificationService = notificationService;
    }

    @Override protected NotificationDTO fetch(NotificationKey key) throws Throwable
    {
        NotificationDTO notificationDTO = notificationService.get().getNotificationDetail(key.key);
        notificationDTO.unread = true;
        return notificationDTO;
    }
}
