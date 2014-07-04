package com.tradehero.th.models.push.handlers;

import android.content.Intent;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.persistence.notification.NotificationCache;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public abstract class PrecacheNotificationHandler implements PushNotificationHandler
{
    @NotNull protected final NotificationCache notificationCache;

    protected NotificationKey getNotificationKey()
    {
        return notificationKey;
    }
    private NotificationKey notificationKey;

    public PrecacheNotificationHandler(@NotNull NotificationCache notificationCache)
    {
        this.notificationCache = notificationCache;
    }

    @Override public boolean handle(@NotNull Intent intent)
    {
        injectNotificationKey(intent);
        return false;
    }

    private void injectNotificationKey(@NotNull Intent intent)
    {
        String notificationIdValue = intent.getStringExtra(PushConstants.KEY_PUSH_ID);
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
                Timber.e(ex, "NotificationId (%s) is not in correct format", notificationIdValue);
            }
        }
    }
}
