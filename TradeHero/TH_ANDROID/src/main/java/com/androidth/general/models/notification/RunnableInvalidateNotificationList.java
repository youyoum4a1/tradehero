package com.androidth.general.models.notification;

import com.androidth.general.base.THApp;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.persistence.notification.NotificationListCacheRx;
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
