package com.tradehero.th.models.push.handlers;

import android.content.Intent;
import com.tradehero.th.persistence.notification.NotificationCache;
import javax.inject.Inject;
import timber.log.Timber;

//import com.urbanairship.push.PushManager;

public class PushReceivedHandler extends PrecacheNotificationHandler
{
    @Inject public PushReceivedHandler(NotificationCache notificationCache)
    {
        super(notificationCache);
    }

    @Override public String getAction()
    {
        return "com.urbanairship.push.PUSH_RECEIVED";
    }

    @Override public boolean handle(Intent intent)
    {
        super.handle(intent);

        int id = intent.getIntExtra("com.urbanairship.push.NOTIFICATION_ID", 0);

        Timber.i("Received push notification. Alert: %s [NotificationID=%d]", intent.getStringExtra("com.urbanairship.push.ALERT"), id);
        return true;
    }

}
