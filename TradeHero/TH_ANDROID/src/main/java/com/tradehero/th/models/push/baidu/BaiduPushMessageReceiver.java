package com.tradehero.th.models.push.baidu;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.tradehero.th.R;
import java.util.List;
import timber.log.Timber;

/**
 */
public class BaiduPushMessageReceiver extends FrontiaPushMessageReceiver
{

    /** TAG to Log */
    public static final String TAG = BaiduPushMessageReceiver.class.getSimpleName();
    public static final int CODE_OK = 0;
    public static final int MESSAGE_ID = 100;

    /**
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appId,
            String userId, String channelId, String requestId)
    {
        Timber.d("onBind appId:%s userId:%s channelId:%s requestId:%d", appId, userId, channelId,
                requestId);
        // 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
        if (!isRequestSuccess(errorCode))
        {
            return;
        }
        //  Bind success
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        new PushSender().updateDeviceIdentifier(appId, userId, channelId);
    }

    private boolean isRequestSuccess(int errorCode)
    {
        return errorCode == CODE_OK;
    }

    private void showNotification(Context context, String message)
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
        cBuilder.setNotificationTitle(context.getApplicationInfo().name);
        cBuilder.setNotificationText(message);
        Notification notification = cBuilder.construct(context);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(MESSAGE_ID, notification);
    }

    private void handleRecevieMessage(Context context, String customContentString)
    {
        showNotification(context, customContentString);
    }

    /**
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

    private void handleMessageClick()
    {

    }

    /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString)
    {
        Timber.d("onNotificationClicked title:%s description:%s customContentString:%s", title,
                description, customContentString);
        handleMessageClick();
    }

    /**
     * setTags() 的回调函数。
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId)
    {
        Timber.d("onSetTags sucessTags:%s failTags:%s requestId:%s", sucessTags, failTags,
                requestId);
    }

    /**
     * delTags() 的回调函数。
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId)
    {
        Timber.d("onDelTags sucessTags:%s failTags:%s requestId:%s", sucessTags, failTags,
                requestId);
    }

    /**
     * listTags() 的回调函数。
     */
    @Override
    public void onListTags(Context context, int errorCode,
            List<String> tags, String requestId)
    {
        Timber.d("onListTags tags:%s", tags);
    }

    /**
     * PushManager.stopWork() 的回调函数。
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId)
    {
        Timber.d("onUnbind errorCode:%s", errorCode);

        // 解绑定成功，设置未绑定flag，
        if (!isRequestSuccess(errorCode))
        {
            return;
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
    }

    private void updateContent(Context context, String content)
    {
    }
}
