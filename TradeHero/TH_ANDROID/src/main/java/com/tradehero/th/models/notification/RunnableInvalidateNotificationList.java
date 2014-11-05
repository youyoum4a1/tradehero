package com.tradehero.th.models.notification;

import com.tradehero.th.persistence.notification.NotificationListCacheRx;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class RunnableInvalidateNotificationList implements Runnable
{
    @Inject @NotNull NotificationListCacheRx notificationListCache;

    //<editor-fold desc="Constructors">
    @Inject public RunnableInvalidateNotificationList(
            @NotNull NotificationListCacheRx notificationListCache)
    {
        this.notificationListCache = notificationListCache;
    }

    public RunnableInvalidateNotificationList()
    {
        DaggerUtils.inject(this);
    }
    //</editor-fold>

    @Override public void run()
    {
        notificationListCache.invalidateAll();
    }
}
