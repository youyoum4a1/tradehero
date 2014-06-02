package com.tradehero.th.models.push.urbanairship;

import com.tradehero.th.models.push.DefaultIntentReceiver;
import com.tradehero.th.models.push.PushConstants.THAction;
import com.urbanairship.push.GCMMessageHandler;
import com.urbanairship.push.PushManager;

public class UrbanAirshipIntentReceiver extends DefaultIntentReceiver
{
    @Override protected THAction translateAction(String action)
    {
        switch (action)
        {
            case GCMMessageHandler.ACTION_GCM_DELETED_MESSAGES:
                return THAction.GcmDeleted;
            case PushManager.ACTION_NOTIFICATION_OPENED:
                return THAction.Opened;
            case PushManager.ACTION_PUSH_RECEIVED:
                return THAction.Received;
            case PushManager.ACTION_REGISTRATION_FINISHED:
                return THAction.RegistrationFinished;
        }

        return null;
    }
}
