package com.androidth.general.models.push.handlers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.activities.DashboardActivity;
import com.androidth.general.api.notification.NotificationKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.models.push.NotificationGroupHolder;
import com.androidth.general.models.push.PushConstants;
import com.androidth.general.persistence.notification.NotificationCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;

public class NotificationOpenedHandler extends PrecacheNotificationHandler
{
    @NonNull private final Context context;
    @NonNull private CurrentUserId currentUserId;
    @NonNull private final UserProfileCacheRx userProfileCache;
    private final NotificationGroupHolder notificationGroupHolder;

    @Inject public NotificationOpenedHandler(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull NotificationCacheRx notificationCache,
            NotificationGroupHolder notificationGroupHolder)
    {
        super(notificationCache);
        this.context = context;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.notificationGroupHolder = notificationGroupHolder;
    }

    @Override public PushConstants.THAction getAction()
    {
        return PushConstants.THAction.Opened;
    }

    @Override public boolean handle(@NonNull Intent intent)
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

        userProfileCache.invalidate(currentUserId.toUserBaseKey());

        context.startActivity(launchIntent);
        return true;
    }

    private void clearNotificationGroup(@NonNull Intent intent)
    {
        int groupId = intent.getIntExtra(PushConstants.KEY_PUSH_GROUP_ID, -1);

        if (groupId > 0)
        {
            notificationGroupHolder.remove(groupId);
        }
    }

    @NonNull private Intent createLaunchIntent(@NonNull Intent intent)
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
