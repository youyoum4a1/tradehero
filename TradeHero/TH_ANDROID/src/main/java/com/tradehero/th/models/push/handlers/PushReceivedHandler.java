package com.tradehero.th.models.push.handlers;

import android.content.Intent;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.persistence.notification.NotificationCacheRx;
import javax.inject.Inject;
import android.support.annotation.NonNull;

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
