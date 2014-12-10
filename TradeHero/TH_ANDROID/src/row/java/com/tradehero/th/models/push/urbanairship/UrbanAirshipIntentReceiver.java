package com.tradehero.th.models.push.urbanairship;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.push.PushConstants.THAction;
import com.tradehero.th.models.push.handlers.PushNotificationHandler;
import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

public class UrbanAirshipIntentReceiver extends BaseIntentReceiver
{
    @Inject Set<PushNotificationHandler> pushNotificationHandlers;
    @NonNull private final Map<THAction, PushNotificationHandler> handlers;

    //<editor-fold desc="Constructors">
    public UrbanAirshipIntentReceiver()
    {
        HierarchyInjector.inject(this);
        handlers = new HashMap<>();
        for (PushNotificationHandler pushNotificationHandler : pushNotificationHandlers)
        {
            handlers.put(pushNotificationHandler.getAction(), pushNotificationHandler);
        }
    }
    //</editor-fold>


    @Override protected void onChannelRegistrationSucceeded(Context context, String s)
    {

    }

    @Override protected void onChannelRegistrationFailed(Context context)
    {

    }

    @Override protected void onPushReceived(Context context, PushMessage pushMessage, int i)
    {
        PushNotificationHandler handler = handlers.get(THAction.Received);
        //handler.handle()
    }

    @Override protected void onBackgroundPushReceived(Context context, PushMessage pushMessage)
    {

    }

    @Override protected boolean onNotificationOpened(Context context, PushMessage pushMessage, int i)
    {
        return false;
    }

    @Override protected boolean onNotificationActionOpened(Context context, PushMessage pushMessage, int i, String s, boolean b)
    {
        return false;
    }

    protected THAction translateAction(String action)
    {
        switch (action)
        {
            //case GCMMessageHandler.ACTION_GCM_DELETED_MESSAGES:
            //    return THAction.GcmDeleted;
            case PushManager.ACTION_NOTIFICATION_OPENED:
                return THAction.Opened;
            case PushManager.ACTION_PUSH_RECEIVED:
                return THAction.Received;
            //case PushManager.ACTION_REGISTRATION_FINISHED:
            //    return THAction.RegistrationFinished;
        }
        return null;
    }
}
