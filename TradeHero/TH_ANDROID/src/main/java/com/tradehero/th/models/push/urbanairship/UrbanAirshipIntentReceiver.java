package com.tradehero.th.models.push.urbanairship;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tradehero.th.models.push.IntentLogger;
import com.tradehero.th.models.push.handlers.PushNotificationHandler;
import com.tradehero.th.utils.DaggerUtils;
import java.util.Set;
import javax.inject.Inject;
import timber.log.Timber;

public class UrbanAirshipIntentReceiver extends BroadcastReceiver
{
    @Inject Set<PushNotificationHandler> pushNotificationHandlers;

    public UrbanAirshipIntentReceiver()
    {
        super();
        DaggerUtils.inject(this);
    }

    @Override public void onReceive(Context context, Intent intent)
    {
        Timber.d("Received new intent: %s", new IntentLogger(intent));
        String action = intent.getAction();

        // TODO design decision: command/delegate pattern?
        for (PushNotificationHandler pushNotificationHandler: pushNotificationHandlers)
        {
            if (action.equals(pushNotificationHandler.getAction()))
            {
                if (pushNotificationHandler.handle(intent))
                {
                    Timber.d("handled by %s\r\n", pushNotificationHandler.getClass());
                    break;
                }
            }
        }
    }
}
