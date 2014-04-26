package com.tradehero.th.models.push.urbanairship.handlers;

import android.content.Intent;
import com.urbanairship.push.GCMMessageHandler;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by thonguyen on 26/4/14.
 */
public class GcmDeletedHandler implements PushNotificationHandler
{
    @Inject public GcmDeletedHandler()
    {
        super();
    }

    @Override public String getAction()
    {
        return GCMMessageHandler.ACTION_GCM_DELETED_MESSAGES;
    }

    @Override public boolean handle(Intent intent)
    {
        Timber.i("The GCM service deleted %s messages.", intent.getStringExtra(GCMMessageHandler.EXTRA_GCM_TOTAL_DELETED));
        return true;
    }
}
