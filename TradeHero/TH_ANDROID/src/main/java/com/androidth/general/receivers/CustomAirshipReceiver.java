package com.androidth.general.receivers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.androidth.general.R;
import com.androidth.general.activities.SplashActivity;
import com.urbanairship.AirshipReceiver;
import com.urbanairship.actions.ActionValue;
import com.urbanairship.push.PushMessage;

/**
 * Created by jeffgan on 13/10/16.
 */

public class CustomAirshipReceiver extends AirshipReceiver {

    public static String NOTIFICATION_OPENED = "Urban Airship Notification Opened";

    public static String MESSAGE = "Message";
    public static String DEEPLINK = "Deeplink";

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
        if(notificationInfo.getMessage().getActions().containsKey("^d")){
            ActionValue data = notificationInfo.getMessage().getActions().get("^d");
            String url = data.getString();
            url = url.replace("TradeHero-Android", context.getString(R.string.intent_scheme));
            Uri uri = Uri.parse(url);
            intent.setData(uri);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        return true;
    }

    @Override
    protected void onNotificationDismissed(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        Log.i(TAG, "Notification dismissed. Alert: " + notificationInfo.getMessage().getAlert() + ". Notification ID: " + notificationInfo.getNotificationId());
    }

    public static void createDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.th_app_logo);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNeutralButton(context.getString(R.string.tab_to_dismiss), null);

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void createDialogWithListener(Activity activity, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.th_app_logo);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNeutralButton(activity.getString(R.string.tab_to_dismiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(activity.getIntent()!=null){
                    try{
                        activity.getIntent().removeExtra(MESSAGE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }


}
