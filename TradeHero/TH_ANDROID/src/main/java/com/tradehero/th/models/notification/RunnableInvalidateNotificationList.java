package com.tradehero.th.models.notification;

import com.tradehero.th.base.THApp;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.notification.NotificationListCacheRx;
import javax.inject.Inject;

public class RunnableInvalidateNotificationList implements Runnable
{
    @Inject NotificationListCacheRx notificationListCache;

    //<editor-fold desc="Constructors">
    public RunnableInvalidateNotificationList()
    {
        HierarchyInjector.inject(THApp.context(), this);
    }
    //</editor-fold>

    @Override public void run()
    {
        notificationListCache.invalidateAll();
    }
}
