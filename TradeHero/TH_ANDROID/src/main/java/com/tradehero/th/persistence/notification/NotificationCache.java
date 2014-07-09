package com.tradehero.th.persistence.notification;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationDTOList;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import dagger.Lazy;
import java.util.List;
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

    @NotNull public NotificationDTOList put(@NotNull List<NotificationDTO> notificationDTOs)
    {
        NotificationDTOList previous = new NotificationDTOList();
        for (@NotNull NotificationDTO notificationDTO : notificationDTOs)
        {
            previous.add(put(notificationDTO.getDTOKey(), notificationDTO));
        }
        return previous;
    }

    @NotNull public NotificationDTOList get(@NotNull List<NotificationKey> notificationKeys)
    {
        NotificationDTOList previous = new NotificationDTOList();
        for (@NotNull NotificationKey notificationDTO : notificationKeys)
        {
            previous.add(get(notificationDTO));
        }
        return previous;
    }
}
