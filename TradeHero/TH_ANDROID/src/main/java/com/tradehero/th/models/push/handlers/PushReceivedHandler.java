package com.tradehero.th.models.push.handlers;

import android.content.Intent;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.persistence.notification.NotificationCache;
import javax.inject.Inject;

public class PushReceivedHandler extends PrecacheNotificationHandler
{
    @Inject public PushReceivedHandler(NotificationCache notificationCache)
    {
        super(notificationCache);
    }

    @Override public PushConstants.THAction getAction()
    {
        return PushConstants.THAction.Received;
    }

    @Override public boolean handle(Intent intent)
    {
        super.handle(intent);
        return true;
    }

}
