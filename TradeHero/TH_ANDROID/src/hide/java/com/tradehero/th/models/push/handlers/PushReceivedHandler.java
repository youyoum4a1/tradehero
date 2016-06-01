package com.ayondo.academy.models.push.handlers;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.ayondo.academy.models.push.PushConstants;
import com.ayondo.academy.persistence.notification.NotificationCacheRx;
import javax.inject.Inject;

public class PushReceivedHandler extends PrecacheNotificationHandler
{
    //<editor-fold desc="Constructors">
    @Inject public PushReceivedHandler(@NonNull NotificationCacheRx notificationCache)
    {
        super(notificationCache);
    }
    //</editor-fold>

    @Override @NonNull public PushConstants.THAction getAction()
    {
        return PushConstants.THAction.Received;
    }

    @Override public boolean handle(@NonNull Intent intent)
    {
        super.handle(intent);
        return true;
    }

}
