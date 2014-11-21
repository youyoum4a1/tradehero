package com.tradehero.th.models.notification;

import android.support.annotation.NonNull;
import com.tradehero.th.persistence.notification.NotificationListCacheRx;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class RunnableInvalidateNotificationList implements Runnable
{
    @Inject @NonNull NotificationListCacheRx notificationListCache;

    //<editor-fold desc="Constructors">
    @Inject public RunnableInvalidateNotificationList(
            @NonNull NotificationListCacheRx notificationListCache)
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
