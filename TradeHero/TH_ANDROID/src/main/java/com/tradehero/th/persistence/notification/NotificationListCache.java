package com.tradehero.th.persistence.notification;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationKeyList;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.network.service.NotificationService;
import com.tradehero.th.persistence.ListCacheMaxSize;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thonguyen on 3/4/14.
 */
@Singleton
public class NotificationListCache extends StraightDTOCache<NotificationListKey, NotificationKeyList>
{
    private final Lazy<NotificationService> notificationService;
    private final Lazy<NotificationCache> notificationCache;

    @Inject public NotificationListCache(
            @ListCacheMaxSize IntPreference maxSize,
            Lazy<NotificationService> notificationService,
            Lazy<NotificationCache> notificationCache
            )
    {
        super(maxSize.get());

        this.notificationService = notificationService;
        this.notificationCache = notificationCache;
    }

    @Override protected NotificationKeyList fetch(NotificationListKey key) throws Throwable
    {
        return putInternal(notificationService.get().getNotifications(key.toMap()));
    }

    private NotificationKeyList putInternal(PaginatedDTO<NotificationDTO> paginatedNotifications)
    {
        List<NotificationDTO> notificationsList = paginatedNotifications.getData();
        NotificationKeyList notificationKeyList = new NotificationKeyList();

        for (NotificationDTO notificationDTO: notificationsList)
        {
            NotificationKey notificationDTOKey = notificationDTO.getDTOKey();
            notificationKeyList.add(notificationDTOKey);

            notificationCache.get().put(notificationDTOKey, notificationDTO);
        }

        return notificationKeyList;
    }
}
