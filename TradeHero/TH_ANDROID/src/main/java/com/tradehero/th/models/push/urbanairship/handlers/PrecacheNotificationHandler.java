package com.tradehero.th.models.push.urbanairship.handlers;

import android.content.Intent;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.models.push.urbanairship.PushConstants;
import com.tradehero.th.persistence.notification.NotificationCache;
import timber.log.Timber;

/**
 * Created by thonguyen on 26/4/14.
 */
public abstract class PrecacheNotificationHandler implements PushNotificationHandler
{
    protected final NotificationCache notificationCache;

    public PrecacheNotificationHandler(NotificationCache notificationCache)
    {
        this.notificationCache = notificationCache;
    }

    @Override public boolean handle(Intent intent)
    {
        injectNotificationKey(intent);
        return false;
    }

    private void injectNotificationKey(Intent intent)
    {
        String notificationIdValue = intent.getStringExtra(PushConstants.PUSH_ID_KEY);
        if (notificationIdValue != null)
        {
            try
            {
                int notificationId = Integer.parseInt(notificationIdValue);

                NotificationKey notificationKey = new NotificationKey(notificationId);
                notificationCache.autoFetch(notificationKey);

                notificationKey.putParameters(intent.getExtras());
            }
            catch (Exception ex)
            {
                Timber.d("NotificationId (%s) is not in correct format: %s", notificationIdValue, ex.getMessage());
            }
        }
    }
}
