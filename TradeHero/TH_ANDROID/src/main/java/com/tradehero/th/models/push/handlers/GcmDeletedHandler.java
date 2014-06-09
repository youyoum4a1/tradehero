package com.tradehero.th.models.push.handlers;

import android.content.Intent;
import com.tradehero.th.models.push.PushConstants;
import javax.inject.Inject;
import timber.log.Timber;

//import com.urbanairship.push.GCMMessageHandler;

public class GcmDeletedHandler implements PushNotificationHandler
{
    @Inject public GcmDeletedHandler()
    {
        super();
    }

    @Override public PushConstants.THAction getAction()
    {
        return PushConstants.THAction.GcmDeleted;
    }

    @Override public boolean handle(Intent intent)
    {
        Timber.i("The GCM service deleted %s messages.", intent.getStringExtra("total_deleted"));
        return true;
    }
}
