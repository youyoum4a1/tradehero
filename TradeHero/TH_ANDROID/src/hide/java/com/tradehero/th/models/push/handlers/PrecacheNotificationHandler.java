package com.androidth.general.models.push.handlers;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.api.notification.NotificationKey;
import com.androidth.general.models.push.PushConstants;
import com.androidth.general.persistence.notification.NotificationCacheRx;
import timber.log.Timber;

public abstract class PrecacheNotificationHandler implements PushNotificationHandler
{
    @NonNull protected final NotificationCacheRx notificationCache;

    protected NotificationKey getNotificationKey()
    {
        return notificationKey;
    }
    private NotificationKey notificationKey;

    public PrecacheNotificationHandler(@NonNull NotificationCacheRx notificationCache)
    {
        this.notificationCache = notificationCache;
    }

    @Override public boolean handle(@NonNull Intent intent)
    {
        injectNotificationKey(intent);
        return false;
    }

    private void injectNotificationKey(@NonNull Intent intent)
    {
        String notificationIdValue = intent.getStringExtra(PushConstants.KEY_PUSH_ID);
        if (notificationIdValue != null)
        {
            try
            {
                int notificationId = Integer.parseInt(notificationIdValue);

                notificationKey = new NotificationKey(notificationId);
                notificationCache.get(notificationKey);
            }
            catch (Exception ex)
            {
                Timber.e(ex, "NotificationId (%s) is not in correct format", notificationIdValue);
            }
        }
    }
}
