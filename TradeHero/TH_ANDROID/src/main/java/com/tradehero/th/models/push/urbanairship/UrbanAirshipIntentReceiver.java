package com.tradehero.th.models.push.urbanairship;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.tradehero.th.models.push.urbanairship.handlers.PushNotificationHandler;
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


    public static class IntentLogger
    {
        private final Intent intent;

        public IntentLogger(Intent intent)
        {
            super();

            this.intent = intent;
        }

        @Override public String toString()
        {
            Bundle extras = intent.getExtras();
            if (extras != null)
            {
                Set<String> keys = extras.keySet();
                StringBuilder sb = new StringBuilder();
                for (String key : keys)
                {
                    sb.append("\r\n")
                            .append(key)
                            .append(": ")
                            .append(intent.getExtras().get(key));
                }
                return sb.toString();
            }
            return null;
        }
    }
}
