package com.tradehero.th.models.notification;

import com.tradehero.th.persistence.notification.NotificationListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class RunnableInvalidateNotificationList implements Runnable
{
    @Inject @NotNull NotificationListCache notificationListCache;

    //<editor-fold desc="Constructors">
    @Inject public RunnableInvalidateNotificationList(
            @NotNull NotificationListCache notificationListCache)
    {
        this.notificationListCache = notificationListCache;
    }

    @Override public void run()
    {
        notificationListCache.invalidateAll();
    }
}
