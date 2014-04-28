package com.tradehero.th.models.push.handlers;

import android.content.Intent;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.models.push.urbanairship.PushConstants;
import com.tradehero.th.persistence.notification.NotificationCache;
import timber.log.Timber;

public abstract class PrecacheNotificationHandler implements PushNotificationHandler
{
    protected final NotificationCache notificationCache;

    protected NotificationKey getNotificationKey()
    {
        return notificationKey;
    }

    private NotificationKey notificationKey;

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

                notificationKey = new NotificationKey(notificationId);
                notificationCache.autoFetch(notificationKey);
            }
            catch (Exception ex)
            {
                Timber.d("NotificationId (%s) is not in correct format: %s", notificationIdValue, ex.getMessage());
            }
        }
    }
}
