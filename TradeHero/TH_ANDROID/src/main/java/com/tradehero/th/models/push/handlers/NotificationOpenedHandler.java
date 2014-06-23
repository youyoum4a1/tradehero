package com.tradehero.th.models.push.handlers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.models.push.NotificationGroupHolder;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.persistence.notification.NotificationCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class NotificationOpenedHandler extends PrecacheNotificationHandler
{
    @NotNull private final Context context;
    private final NotificationGroupHolder notificationGroupHolder;

    @Inject public NotificationOpenedHandler(
            @NotNull Context context,
            @NotNull NotificationCache notificationCache,
            NotificationGroupHolder notificationGroupHolder)
    {
        super(notificationCache);
        this.context = context;
        this.notificationGroupHolder = notificationGroupHolder;
    }

    @Override public PushConstants.THAction getAction()
    {
        return PushConstants.THAction.Opened;
    }

    @Override public boolean handle(@NotNull Intent intent)
    {
        super.handle(intent);

        Intent launchIntent = createLaunchIntent(intent);

        NotificationKey notificationKey = getNotificationKey();
        if (notificationKey != null)
        {
            launchIntent.putExtras(notificationKey.getArgs());
        }

        // remove the its group
        clearNotificationGroup(intent);

        context.startActivity(launchIntent);
        return true;
    }

    private void clearNotificationGroup(@NotNull Intent intent)
    {
        int groupId = intent.getIntExtra(PushConstants.KEY_PUSH_GROUP_ID, -1);

        if (groupId > 0)
        {
            notificationGroupHolder.remove(groupId);
        }
    }

    @NotNull private Intent createLaunchIntent(@NotNull Intent intent)
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
