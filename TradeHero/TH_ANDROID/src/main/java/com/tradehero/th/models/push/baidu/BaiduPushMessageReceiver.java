package com.tradehero.th.models.push.baidu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.models.push.handlers.NotificationOpenedHandler;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.prefs.BaiduPushDeviceIdentifierSentFlag;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class BaiduPushMessageReceiver extends FrontiaPushMessageReceiver
{
    public static final String ACTION_NOTIFICATION_CLICKED = "com.tradehero.th.ACTION_NOTIFICATION_CLICKED";
    public static final String KEY_NOTIFICATION_ID = "com.tradehero.th.NOTIFICATION_ID";
    public static final String KEY_NOTIFICATION_CONTENT = "com.tradehero.th.NOTIFICATION_CONTENT";

    public static final int CODE_OK = 0;

    @Inject Provider<CustomPushNotificationBuilder> customPushNotificationBuilderProvider;
    @Inject CurrentUserId currentUserId;
    @Inject SessionServiceWrapper sessionServiceWrapper;

    @Inject @BaiduPushDeviceIdentifierSentFlag BooleanPreference pushDeviceIdentifierSentFlag;
    @Inject @SavedPushDeviceIdentifier StringPreference savedPushDeviceIdentifier;

    @Inject static Provider<NotificationOpenedHandler> notificationOpenedHandler;

    public BaiduPushMessageReceiver()
    {
        DaggerUtils.inject(this);
    }

    /**
     * After calling PushManager.startWork (or BaiduPushManager.enablePush), BaiduSDK will send to server a asynchronous request asking for binding.
     * Binding request results returned by onBind.
     *
     * @param channelId: used for unicast push (push to QQ/Wechat/... network
     * @param userId: used for unicast push
     */
    @Override public void onBind(Context context, int errorCode, String appId, String userId, String channelId, String requestId)
    {
        Timber.d("onBind appId: %s, userId: %s, channelId: %s, requestId: %s", appId, userId, channelId, requestId);
        if (errorCode == CODE_OK)
        {
            updateDeviceIdentifier(appId, userId, channelId);
        }
    }

    /**
     * Callback for PushManager.stopWork() (or BaiduPushManager.disablePush)
     */
    @Override public void onUnbind(Context context, int errorCode, String requestId)
    {
        Timber.d("onUnbind errorCode:%s", errorCode);
        if (errorCode == CODE_OK)
        {
            setPushDeviceIdentifierSentFlag(false);
        }
    }

    /**
     * When a message is received
     */
    @Override public void onMessage(Context context, String message, String customContentString)
    {
        Timber.d("onMessage message: %s, customContentString: %s", message, customContentString);
        if (!TextUtils.isEmpty(message))
        {
            handleReceiveMessage(context, message);
        }
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
        int id = intent.getIntExtra(KEY_NOTIFICATION_ID, -1);
        String description = intent.getStringExtra(KEY_NOTIFICATION_CONTENT);
        Timber.d("action: %s, id: %s, description: %s", action, id, description);

        Intent fakeIntent = new Intent();
        fakeIntent.putExtra(PushConstants.PUSH_ID_KEY, String.valueOf(id));
        notificationOpenedHandler.get().handle(fakeIntent);

        return intent;
    }

    private void showNotification(Context context, PushMessageDTO pushMessageDTO)
    {
        CustomPushNotificationBuilder customPushNotificationBuilder = customPushNotificationBuilderProvider.get();
        customPushNotificationBuilder.setNotificationText(pushMessageDTO.description);

        Notification notification = customPushNotificationBuilder.construct(context);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //TODO if we set PendingIntent.FLAG_ONE_SHOT, only the first notification will jump to new fragment. So temp remove it by alex
        notification.contentIntent = PendingIntent.getBroadcast(context, 0, composeIntent(pushMessageDTO), 0);
        //notification.contentIntent = PendingIntent.getBroadcast(context, 0, composeIntent(pushMessageDTO), PendingIntent.FLAG_ONE_SHOT);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int msgId;
        if (pushMessageDTO.id > 0)
        {
            msgId = pushMessageDTO.id;
        }
        else
        {
            int hashCode = Math.abs(pushMessageDTO.description.hashCode());
            msgId = hashCode % 1000;
        }
        Timber.d("Msg id:%d,content:%s",msgId,pushMessageDTO.description);
        nm.notify(msgId, notification);
    }

    private void handleReceiveMessage(Context context, String message)
    {
        PushMessageDTO pushMessageDTO = PushMessageHandler.parseNotification(message);
        if (pushMessageDTO != null)
        {
            switch (pushMessageDTO.discussionType)
            {
                case BROADCAST_MESSAGE:
                case PRIVATE_MESSAGE:
                    PushMessageHandler.notifyMessageReceived(context);
                    break;
            }
            showNotification(context, pushMessageDTO);
        }
    }

    public void updateDeviceIdentifier(String appId,String userId, String channelId)
    {
        if (currentUserId == null)
        {
            Timber.e("Current user is null, quit");
            return;
        }
        if (pushDeviceIdentifierSentFlag.get())
        {
            Timber.d("Already send the device token to the server, quit");
            return;
        }

        BaiduDeviceMode deviceMode = new BaiduDeviceMode(channelId, userId, appId);
        savedPushDeviceIdentifier.set(deviceMode.token);
        sessionServiceWrapper.updateDevice(new UpdateDeviceIdentifierCallback());
    }

    public void setPushDeviceIdentifierSentFlag(boolean bind)
    {
        pushDeviceIdentifierSentFlag.set(bind);
    }

    class UpdateDeviceIdentifierCallback implements Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            Timber.d("UpdateDeviceIdentifierCallback send success");
            setPushDeviceIdentifierSentFlag(true);
        }

        @Override public void failure(RetrofitError error)
        {
            Timber.e(error,"UpdateDeviceIdentifierCallback send failure");
            setPushDeviceIdentifierSentFlag(false);
        }
    }

    //<editor-fold desc="Not being used for the time being">

    /**
     * when user click the notification
     */
    @Override public void onNotificationClicked(Context context, String title, String description, String customContentString)
    {
        Timber.d("onNotificationClicked title:%s, description:%s, customContentString:%s", title, description, customContentString);
    }

    /**
     * Callback for setTags()
     */
    @Override public void onSetTags(Context context, int errorCode, List<String> successTags, List<String> failTags, String requestId)
    {
        Timber.d("onSetTags successTags: %s, failTags: %s, requestId:%s", successTags, failTags, requestId);
    }

    /**
     * Callback for delTags()
     */
    @Override public void onDelTags(Context context, int errorCode, List<String> successTags, List<String> failTags, String requestId)
    {
        Timber.d("onDelTags successTags:%s failTags:%s requestId:%s", successTags, failTags, requestId);
    }

    /**
     * Callback for listTags()
     */
    @Override public void onListTags(Context context, int errorCode, List<String> tags, String requestId)
    {
        Timber.d("onListTags tags:%s", tags);
    }
    //</editor-fold>
}
