package com.tradehero.th.models.push.urbanairship;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import com.tradehero.th.R;
import com.tradehero.th.models.push.CommonNotificationBuilder;
import com.urbanairship.push.PushMessage;
import com.urbanairship.push.notifications.NotificationFactory;
import com.urbanairship.util.NotificationIDGenerator;

public class THNotificationFactory extends NotificationFactory
{
    @NonNull private final CommonNotificationBuilder commonNotificationBuilder;

    public THNotificationFactory(
            @NonNull Context context,
            @NonNull CommonNotificationBuilder commonNotificationBuilder)
    {
        super(context);
        this.commonNotificationBuilder = commonNotificationBuilder;
    }

    @Override public Notification createNotification(PushMessage pushMessage, int notificationId)
    {
        return commonNotificationBuilder.buildNotification(notificationId);
        // Build the notification
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
        //        .setContentTitle(getContext().getString(R.string.app_name))
        //        .setContentText(pushMessage.getAlert())
        //        .setAutoCancel(true)
        //        .setSmallIcon(R.drawable.th_logo);

        // To support interactive notification buttons extend the NotificationCompat.Builder
        //builder.extend(createNotificationActionsExtender(pushMessage, notificationId));

        //return builder.build();
    }

    @Override public int getNextId(PushMessage pushMessage)
    {
        return NotificationIDGenerator.nextID();
    }
}
