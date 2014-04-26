package com.tradehero.th.models.push.urbanairship.handlers;

import android.content.Intent;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by thonguyen on 26/4/14.
 */
public class PushReceivedHandler extends PrecacheNotificationHandler
{
    @Inject public PushReceivedHandler(NotificationCache notificationCache)
    {
        super(notificationCache);
    }

    @Override public String getAction()
    {
        return PushManager.ACTION_PUSH_RECEIVED;
    }

    @Override public boolean handle(Intent intent)
    {
        super.handle(intent);

        int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);

        Timber.i("Received push notification. Alert: %s [NotificationID=%d]", intent.getStringExtra(PushManager.EXTRA_ALERT), id);
        return true;
    }

}
