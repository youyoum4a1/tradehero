package com.tradehero.th.models.push.urbanairship.handlers;

import android.content.Intent;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.models.push.urbanairship.PushConstants;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by thonguyen on 26/4/14.
 */
public class PushReceivedHandler implements PushNotificationHandler
{
    private final NotificationCache notificationCache;

    @Inject public PushReceivedHandler(NotificationCache notificationCache)
    {
        super();

        this.notificationCache = notificationCache;
    }

    @Override public String getAction()
    {
        return PushManager.ACTION_PUSH_RECEIVED;
    }

    @Override public boolean handle(Intent intent)
    {
        int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);

        // pre cache data
        if (intent.hasExtra(PushConstants.PUSH_ID_KEY))
        {
            int notificationId = intent.getIntExtra(PushConstants.PUSH_ID_KEY, -1);
            if (notificationId >= 0)
            {
                notificationCache.autoFetch(new NotificationKey(notificationId));
            }
        }

        Timber.i("Received push notification. Alert: %s [NotificationID=%d]", intent.getStringExtra(PushManager.EXTRA_ALERT), id);
        return true;
    }
}
