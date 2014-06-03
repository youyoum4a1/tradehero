package com.tradehero.th.models.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tradehero.th.models.push.handlers.PushNotificationHandler;
import com.tradehero.th.utils.DaggerUtils;
import java.util.Set;
import javax.inject.Inject;
import timber.log.Timber;

public abstract class DefaultIntentReceiver extends BroadcastReceiver
{
    @Inject Set<PushNotificationHandler> pushNotificationHandlers;

    public DefaultIntentReceiver()
    {
        super();
        DaggerUtils.inject(this);
    }

    @Override public void onReceive(Context context, Intent intent)
    {
        Timber.d("Received new intent: %s, using %s", new IntentLogger(intent), getClass().getName());
        PushConstants.THAction action = translateAction(intent.getAction());

        // TODO design decision: command/delegate pattern?
        for (PushNotificationHandler pushNotificationHandler: pushNotificationHandlers)
        {
            if (action == pushNotificationHandler.getAction())
            {
                if (pushNotificationHandler.handle(intent))
                {
                    Timber.d("handled by %s\r\n", pushNotificationHandler.getClass());
                    break;
                }
            }
        }
    }

    protected PushConstants.THAction translateAction(String action)
    {
        switch (action)
        {
            case PushConstants.ACTION_NOTIFICATION_CLICKED:
                return PushConstants.THAction.Opened;
        }

        Timber.d("Unknown intent action: %s", action);
        return null;
    }
}
