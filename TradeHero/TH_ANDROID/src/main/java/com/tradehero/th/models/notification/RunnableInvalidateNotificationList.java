package com.tradehero.th.models.notification;

import com.tradehero.th.persistence.notification.NotificationListCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class RunnableInvalidateNotificationList implements Runnable
{
    @Inject NotificationListCache notificationListCache;

    //<editor-fold desc="Constructors">
    @Inject public RunnableInvalidateNotificationList(
            NotificationListCache notificationListCache)
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
