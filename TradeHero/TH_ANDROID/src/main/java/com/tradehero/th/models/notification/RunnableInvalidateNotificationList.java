package com.ayondo.academy.models.notification;

import com.ayondo.academy.base.THApp;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.persistence.notification.NotificationListCacheRx;
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
