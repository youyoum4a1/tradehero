package com.androidth.general.models.push.handlers;

import android.content.Intent;
import com.androidth.general.models.push.PushConstants;
import javax.inject.Inject;

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
        //Timber.i("The GCM service deleted %s messages.", intent.getStringExtra(GCMMessageHandler.EXTRA_GCM_TOTAL_DELETED));
        return true;
    }
}
