package com.tradehero.th.models.push.urbanairship;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tradehero.th.models.push.urbanairship.handlers.PushNotificationHandler;
import com.tradehero.th.utils.DaggerUtils;
import com.urbanairship.push.PushManager;
import java.util.Arrays;
import java.util.List;
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
        Timber.i("Received intent: %s", intent.toString());
        String action = intent.getAction();

        // TODO design decision: command/delegate pattern?
        for (PushNotificationHandler pushNotificationHandler: pushNotificationHandlers)
        {
            if (action.equals(pushNotificationHandler.getAction()))
            {
                if (pushNotificationHandler.handle(intent))
                {
                    break;
                }
            }
        }
    }

    /**
     * Log the values sent in the payload's "extra" dictionary.
     *
     * @param intent A PushManager.ACTION_NOTIFICATION_OPENED or ACTION_PUSH_RECEIVED intent.
     */
    private void logPushExtras(Intent intent)
    {
        Set<String> keys = intent.getExtras().keySet();
        for (String key : keys)
        {

            //ignore standard C2DM extra keys
            List<String> ignoredKeys = (List<String>) Arrays.asList(
                    "collapse_key",//c2dm collapse key
                    "from",//c2dm sender
                    PushManager.EXTRA_NOTIFICATION_ID,//int id of generated notification (ACTION_PUSH_RECEIVED only)
                    PushManager.EXTRA_PUSH_ID,//internal UA push id
                    PushManager.EXTRA_ALERT);//ignore alert
            if (ignoredKeys.contains(key))
            {
                continue;
            }
            Timber.i("Push Notification Extra: [%s : %s]", key, intent.getStringExtra(key));
        }
    }
}
