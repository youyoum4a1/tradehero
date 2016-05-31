package com.tradehero.th.models.push.urbanairship;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushMessage;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class UrbanAirshipIntentReceiver extends BaseIntentReceiver
{
    /**
     * Key passed in the intent when a channel id has been assigned. The value is the channel id string, like "2dea130a-c5b8-46ad-b065-be6603050d28"
     */
    private static final String KEY_CHANNEL_ID = "com.urbanairship.push.EXTRA_CHANNEL_ID";

    @Override public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        List<String> extras = new ArrayList<>();
        Bundle extrasBundle = intent.getExtras();
        if (extrasBundle != null)
        {
            for (String key : extrasBundle.keySet())
            {
                extras.add(String.format("%s: %s", key, extrasBundle.get(key)));
            }
        }
        Timber.e(new JustReportingException("Just reporting"),
                "UrbanAirshipIntentReceiver.onReceive intent %s, category: %s, keys: %s",
                intent.getAction(),
                intent.getCategories().iterator().next(),
                TextUtils.join(", ", extras)
        );
    }

    @Override protected void onChannelRegistrationSucceeded(Context context, String s)
    {
        Timber.e(new JustReportingException("Just reporting"),
                "UrbanAirshipIntentReceiver.onChannelRegistrationSucceeded " + s);
    }

    @Override protected void onChannelRegistrationFailed(Context context)
    {
        Timber.e(new JustReportingException("Just reporting"),
                "UrbanAirshipIntentReceiver.onChannelRegistrationFailed");
    }

    @Override protected void onPushReceived(Context context, PushMessage pushMessage, int i)
    {
        Timber.e(new JustReportingException("Just reporting"),
                "UrbanAirshipIntentReceiver.onPushReceived i=" + i + ", message: " + pushMessage.getPublicNotificationPayload());
    }

    @Override protected void onBackgroundPushReceived(Context context, PushMessage pushMessage)
    {
        Timber.e(new JustReportingException("Just reporting"),
                "UrbanAirshipIntentReceiver.onBackgroundPushReceived message: " + pushMessage.getPublicNotificationPayload());
    }

    @Override protected boolean onNotificationOpened(Context context, PushMessage pushMessage, int i)
    {
        Timber.e(new JustReportingException("Just reporting"),
                "UrbanAirshipIntentReceiver.onNotificationOpened i=" + i + ", message: " + pushMessage.getPublicNotificationPayload());
        return false;
    }

    @Override protected boolean onNotificationActionOpened(Context context, PushMessage pushMessage, int i, String s, boolean b)
    {
        Timber.e(new JustReportingException("Just reporting"),
                "UrbanAirshipIntentReceiver.onNotificationActionOpened i=" + i + ", b=" + b + ", message: " + pushMessage.getPublicNotificationPayload());
        return false;
    }

    @Override protected void onNotificationDismissed(Context context, PushMessage message, int notificationId)
    {
        super.onNotificationDismissed(context, message, notificationId);
        Timber.e(new JustReportingException("Just reporting"),
                "UrbanAirshipIntentReceiver.onNotificationDismissed id=" + notificationId + ", message: " + message.getPublicNotificationPayload());
    }
}
