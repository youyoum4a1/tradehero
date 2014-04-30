package com.tradehero.th.models.push.baidu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.models.push.handlers.NotificationOpenedHandler;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import timber.log.Timber;

public class BaiduPushMessageReceiver extends FrontiaPushMessageReceiver
{
    public static final String ACTION_NOTIFICATION_CLICKED = "com.tradehero.th.ACTION_NOTIFICATION_CLICKED";
    public static final String KEY_NOTIFICATION_ID = "com.tradehero.th.NOTIFICATION_ID";
    public static final String KEY_NOTIFICATION_CONTENT = "com.tradehero.th.NOTIFICATION_CONTENT";

    public static final int CODE_OK = 0;
    public static final int MESSAGE_ID = 100;

    @Inject Provider<PushSender> pushSender;
    @Inject static Provider<NotificationOpenedHandler> notificationOpenedHandler;

    public BaiduPushMessageReceiver()
    {
        DaggerUtils.inject(this);
    }

    /**
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appId,
            String userId, String channelId, String requestId)
    {
        Timber.d("onBind appId:%s userId:%s channelId:%s requestId:%s", appId, userId, channelId, requestId);
        //if bind successfully, don't have to bind again
        if (!isRequestSuccess(errorCode))
        {
            return;
        }
        pushSender.get().updateDeviceIdentifier(appId, userId, channelId);
    }

    /**
     * Callback for PushManager.stopWork()
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId)
    {
        Timber.d("onUnbind errorCode:%s", errorCode);
        if (!isRequestSuccess(errorCode))
        {
            return;
        }
        // onUnbind success
        pushSender.get().setPushDeviceIdentifierSentFlag(false);
        // do your own logic
    }


    public static Intent composeIntent(PushMessageDTO pushMessageDTO)
    {
        Intent intent = new Intent(ACTION_NOTIFICATION_CLICKED);
        intent.putExtra(KEY_NOTIFICATION_ID, pushMessageDTO.id);
        intent.putExtra(KEY_NOTIFICATION_CONTENT, pushMessageDTO.description);
        return intent;
    }


    public static Intent handleIntent(Intent intent)
    {
        String action = intent.getAction();
        int id = intent.getIntExtra(KEY_NOTIFICATION_ID,-1);
        String description = intent.getStringExtra(KEY_NOTIFICATION_CONTENT);
        Timber.d("action %s, id:%s, description:%s",action,id,description);

        Intent fakeIntent = new Intent();
        fakeIntent.putExtra(PushConstants.PUSH_ID_KEY, String.valueOf(id));
        notificationOpenedHandler.get().handle(fakeIntent);

        return intent;
    }

    private boolean isRequestSuccess(int errorCode)
    {
        return errorCode == CODE_OK;
    }

    private void showNotification(Context context, PushMessageDTO pushMessageDTO)
    {
        com.baidu.android.pushservice.CustomPushNotificationBuilder
                cBuilder = new com.baidu.android.pushservice.CustomPushNotificationBuilder(
                context.getApplicationContext(),
                R.layout.notification,
                R.id.notification_icon,
                R.id.notification_subject,
                R.id.message
        );

        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(R.drawable.notification_logo);
        cBuilder.setLayoutDrawable(R.drawable.notification_logo);
        //cBuilder.setNotificationTitle(context.getApplicationInfo().name);
        cBuilder.setNotificationTitle(context.getString(R.string.app_name));
        cBuilder.setNotificationText(pushMessageDTO.description);
        Notification notification = cBuilder.construct(context);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.contentIntent = PendingIntent.getBroadcast(context, 0,composeIntent(pushMessageDTO), 0);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int hashCode = pushMessageDTO.description.hashCode();
        if (hashCode < 0)
        {
            hashCode = -hashCode;
        }
        int msgId = hashCode % 1000;
        nm.notify(msgId, notification);

    }


    private void handleRecevieMessage(Context context, String message)
    {
        PushMessageDTO pushMessageDTO  = PushMessageHandler.parseNotification(message);
        if (pushMessageDTO != null)
        {
            if (pushMessageDTO.discussionType == DiscussionType.BROADCAST_MESSAGE || pushMessageDTO.discussionType == DiscussionType.PRIVATE_MESSAGE)
            {
                PushMessageHandler.notifyMessageReceived(context);
            }
            showNotification(context, pushMessageDTO);
        }

    }

    /**
     * when user receive message
     */
    @Override
    public void onMessage(Context context, String message, String customContentString)
    {
        Timber.d("onMessage message:%s customContentString:%s", message, customContentString);
        if (!TextUtils.isEmpty(message))
        {
            handleRecevieMessage(context, message);
        }
    }

    /**
     * when user click the notification
     * currently it's useless
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString)
    {
        Timber.d("onNotificationClicked title:%s description:%s customContentString:%s", title, description, customContentString);
        //handleMessageClick();
    }

    /**
     * Callback for setTags()
     * currently it's useless
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId)
    {
        Timber.d("onSetTags sucessTags:%s failTags:%s requestId:%s", sucessTags, failTags,
                requestId);
    }

    /**
     *  Callback for delTags()
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId)
    {
        Timber.d("onDelTags sucessTags:%s failTags:%s requestId:%s", sucessTags, failTags,
                requestId);
    }

    /**
     * Callback for listTags()
     */
    @Override
    public void onListTags(Context context, int errorCode,
            List<String> tags, String requestId)
    {
        Timber.d("onListTags tags:%s", tags);
    }
}
