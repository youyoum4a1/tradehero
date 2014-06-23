package com.tradehero.th.models.push.handlers;

import android.content.Intent;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.persistence.notification.NotificationCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class PushReceivedHandler extends PrecacheNotificationHandler
{
    //<editor-fold desc="Constructors">
    @Inject public PushReceivedHandler(@NotNull NotificationCache notificationCache)
    {
        super(notificationCache);
    }
    //</editor-fold>

    @Override @NotNull public PushConstants.THAction getAction()
    {
        return PushConstants.THAction.Received;
    }

    @Override public boolean handle(@NotNull Intent intent)
    {
        super.handle(intent);
        return true;
    }

}
