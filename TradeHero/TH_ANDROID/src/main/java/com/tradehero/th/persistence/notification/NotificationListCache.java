package com.tradehero.th.persistence.notification;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationKeyList;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class NotificationListCache extends StraightDTOCacheNew<NotificationListKey, NotificationKeyList>
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

    @Override @NotNull public NotificationKeyList fetch(@NotNull NotificationListKey key) throws Throwable
    {
        return putInternal(notificationService.get().getNotifications(key));
    }

    @NotNull private NotificationKeyList putInternal(@NotNull PaginatedDTO<NotificationDTO> paginatedNotifications)
    {
        List<NotificationDTO> notificationsList = paginatedNotifications.getData();
        NotificationKeyList notificationKeyList = new NotificationKeyList();

        if (notificationsList != null)
        {
            for (@NotNull NotificationDTO notificationDTO: notificationsList)
            {
                NotificationKey notificationDTOKey = notificationDTO.getDTOKey();
                notificationKeyList.add(notificationDTOKey);

                notificationCache.get().put(notificationDTOKey, notificationDTO);
            }
        }

        return notificationKeyList;
    }
}
