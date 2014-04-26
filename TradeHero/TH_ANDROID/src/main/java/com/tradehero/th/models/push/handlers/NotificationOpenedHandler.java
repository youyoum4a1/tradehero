package com.tradehero.th.models.push.handlers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;

/**
 * Created by thonguyen on 26/4/14.
 */
public class NotificationOpenedHandler extends PrecacheNotificationHandler
{
    private final Context context;

    @Inject public NotificationOpenedHandler(Context context, NotificationCache notificationCache)
    {
        super(notificationCache);
        this.context = context;
    }

    @Override public String getAction()
    {
        return PushManager.ACTION_NOTIFICATION_OPENED;
    }

    @Override public boolean handle(Intent intent)
    {
        super.handle(intent);

        Intent launchIntent = createLaunchIntent(intent);

        NotificationKey notificationKey = getNotificationKey();
        if (notificationKey != null)
        {
            launchIntent.putExtras(notificationKey.getArgs());
        }

        context.startActivity(launchIntent);
        return true;
    }

    private Intent createLaunchIntent(Intent intent)
    {
        Intent launch = new Intent(Intent.ACTION_MAIN);
        launch.setClass(context, DashboardActivity.class);
        launch.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.getExtras() != null)
        {
            String deepLink = (String) intent.getExtras().get(context.getString(R.string.push_notification_deep_link_url));
            if (deepLink != null)
            {
                launch.setData(Uri.parse(deepLink));
            }
        }
        return launch;
    }
}
