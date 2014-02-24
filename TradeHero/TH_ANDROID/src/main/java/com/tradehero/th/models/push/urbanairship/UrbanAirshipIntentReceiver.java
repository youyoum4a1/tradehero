/*
Copyright 2009-2011 Urban Airship Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE URBAN AIRSHIP INC ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL URBAN AIRSHIP INC OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.tradehero.th.models.push.urbanairship;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.base.Application;
import com.urbanairship.UAirship;
import com.urbanairship.push.GCMMessageHandler;
import com.urbanairship.push.PushManager;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import timber.log.Timber;

public class UrbanAirshipIntentReceiver extends BroadcastReceiver
{
    public static String APID_UPDATED_ACTION_SUFFIX = ".apid.updated";

    @Override public void onReceive(Context context, Intent intent)
    {
        Timber.i("Received intent: %s", intent.toString());
        String action = intent.getAction();

        if (action.equals(PushManager.ACTION_PUSH_RECEIVED))
        {
            handlePushReceived(context, intent);
        }
        else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED))
        {
            handleNotificationOpened(context, intent);
        }
        else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED))
        {
            handleRegistrationFinished(context, intent);
        }
        else if (action.equals(GCMMessageHandler.ACTION_GCM_DELETED_MESSAGES))
        {
            Timber.i("The GCM service deleted %s messages.", intent.getStringExtra(GCMMessageHandler.EXTRA_GCM_TOTAL_DELETED));
        }
    }

    private void handlePushReceived(Context context, Intent intent)
    {
        int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);

        Timber.i("Received push notification. Alert: %s [NotificationID=%d]", intent.getStringExtra(PushManager.EXTRA_ALERT), id);

        logPushExtras(intent);
    }

    private void handleNotificationOpened(Context context, Intent intent)
    {
        Timber.i("User clicked notification. Message: %s", intent.getStringExtra(PushManager.EXTRA_ALERT));

        logPushExtras(intent);

        UAirship.shared().getApplicationContext().startActivity(createLaunchIntent(intent));
    }

    private void handleRegistrationFinished(Context context, Intent intent)
    {
        Timber.i("Registration complete. APID: %s. Valid: %b",
                intent.getStringExtra(PushManager.EXTRA_APID),
                intent.getBooleanExtra(PushManager.EXTRA_REGISTRATION_VALID, false));

        // Notify any app-specific listeners
        Intent launch = new Intent(UAirship.getPackageName() + APID_UPDATED_ACTION_SUFFIX);
        UAirship.shared().getApplicationContext().sendBroadcast(launch);
    }

    private Intent createLaunchIntent(Intent intent)
    {
        Intent launch = new Intent(Intent.ACTION_MAIN);
        launch.setClass(UAirship.shared().getApplicationContext(), DashboardActivity.class);
        launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String deepLink = (String) intent.getExtras().get(Application.getResourceString(R.string.push_notification_deep_link_url));
        if (deepLink != null)
        {
            launch.setData(Uri.parse(deepLink));
        }
        return launch;
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
