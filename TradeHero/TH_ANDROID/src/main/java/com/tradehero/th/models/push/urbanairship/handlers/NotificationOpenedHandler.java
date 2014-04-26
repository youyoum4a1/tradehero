package com.tradehero.th.models.push.urbanairship.handlers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by thonguyen on 26/4/14.
 */
public class NotificationOpenedHandler implements PushNotificationHandler
{
    private final Context context;

    @Inject public NotificationOpenedHandler(Context context)
    {
        this.context = context;
    }

    @Override public String getAction()
    {
        return PushManager.ACTION_NOTIFICATION_OPENED;
    }

    @Override public boolean handle(Intent intent)
    {
        Timber.i("User clicked notification. Message: %s", intent.getStringExtra(PushManager.EXTRA_ALERT));

        //UAirship.shared().getApplicationContext().startActivity(createLaunchIntent(intent));

        // TODO is this is better??
        context.startActivity(createLaunchIntent(intent));
        return true;
    }

    private Intent createLaunchIntent(Intent intent)
    {
        Intent launch = new Intent(Intent.ACTION_MAIN);
        launch.setClass(context, DashboardActivity.class);
        launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String deepLink = (String) intent.getExtras().get(context.getString(R.string.push_notification_deep_link_url));
        if (deepLink != null)
        {
            launch.setData(Uri.parse(deepLink));
        }
        return launch;
    }
}
