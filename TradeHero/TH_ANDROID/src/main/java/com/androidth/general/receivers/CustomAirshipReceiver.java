package com.androidth.general.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.androidth.general.R;
import com.androidth.general.activities.SplashActivity;
import com.urbanairship.AirshipReceiver;
import com.urbanairship.push.PushMessage;

/**
 * Created by jeffgan on 13/10/16.
 */

public class CustomAirshipReceiver extends AirshipReceiver {

    public static String NOTIFICATION_OPENED = "Urban Airship Notification Opened";

    public static String MESSAGE = "Message";

    private static final String TAG = "CustomAirshipReceiver";

    @Override
    protected void onChannelCreated(@NonNull Context context, @NonNull String channelId) {
        Log.i(TAG, "Channel created. Channel Id:" + channelId + ".");
    }

    @Override
    protected void onChannelUpdated(@NonNull Context context, @NonNull String channelId) {
        Log.i(TAG, "Channel updated. Channel Id:" + channelId + ".");
    }

    @Override
    protected void onChannelRegistrationFailed(Context context) {
        Log.i(TAG, "Channel registration failed.");
    }

    @Override
    protected void onPushReceived(@NonNull Context context, @NonNull PushMessage message, boolean notificationPosted) {
        Log.i(TAG, "Received push message. Alert: " + message.getAlert() + ". posted notification: " + notificationPosted);
        Log.i(TAG, "Received push message. Alert inapp: " + message.getInAppMessage());
    }

    @Override
    protected void onNotificationPosted(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        Log.i(TAG, "Notification posted. Alert: " + notificationInfo.getMessage().getAlert() + ". NotificationId: " + notificationInfo.getNotificationId());
    }

    @Override
    protected boolean onNotificationOpened(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        Log.i(TAG, "Notification opened. Alert: " + notificationInfo.getMessage().getAlert() + ". NotificationId: " + notificationInfo.getNotificationId());

        Log.i(TAG, "Notification opened. Actions: " + notificationInfo.getMessage().getActions());

        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(NOTIFICATION_OPENED);
        intent.putExtra(CustomAirshipReceiver.MESSAGE, notificationInfo.getMessage().getAlert());
        context.startActivity(intent);

        // Return false here to allow Urban Airship to auto launch the launcher activity
        return true;
    }

    @Override
    protected boolean onNotificationOpened(@NonNull Context context, @NonNull NotificationInfo notificationInfo, @NonNull ActionButtonInfo actionButtonInfo) {
        Log.i(TAG, "Notification action button opened. Button ID: " + actionButtonInfo.getButtonId() + ". NotificationId: " + notificationInfo.getNotificationId());

        // Return false here to allow Urban Airship to auto launch the launcher
        // activity for foreground notification action buttons
        return false;
    }

    @Override
    protected void onNotificationDismissed(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        Log.i(TAG, "Notification dismissed. Alert: " + notificationInfo.getMessage().getAlert() + ". Notification ID: " + notificationInfo.getNotificationId());
    }

    public static void createDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.th_app_logo);
        builder.setMessage(message);
        builder.setNeutralButton(context.getString(R.string.tab_to_dismiss), null);

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

}
