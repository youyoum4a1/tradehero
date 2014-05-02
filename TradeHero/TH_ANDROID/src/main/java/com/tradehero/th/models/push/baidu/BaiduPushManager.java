package com.tradehero.th.models.push.baidu;

import android.app.Notification;
import android.content.Context;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.baidu.frontia.FrontiaApplication;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.utils.ForBaiduPush;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class BaiduPushManager implements PushNotificationManager
{
    private static final int NOTIFICATION_BUILDER_ID = 1;

    private final Context context;
    private final CustomPushNotificationBuilder customPushNotificationBuilder;
    private final String appKey;

    @Inject public BaiduPushManager(Context context, CustomPushNotificationBuilder customPushNotificationBuilder, @ForBaiduPush String appKey)
    {
        this.context = context;
        this.customPushNotificationBuilder = customPushNotificationBuilder;
        this.appKey = appKey;
    }

    @Override public void initialise()
    {
        FrontiaApplication.initFrontiaApplication(context);
    }

    @Override public void enablePush()
    {
        Timber.d("enablePush(PushManager.startWork) context:%s, appKey:%s", context, appKey);
        PushSettings.enableDebugMode(context, true);
        PushManager.setNotificationBuilder(context, NOTIFICATION_BUILDER_ID, customPushNotificationBuilder);
        PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY, appKey);
    }

    @Override public void disablePush()
    {
        PushManager.stopWork(context);
    }

    @Override public void setSoundEnabled(boolean enabled)
    {
        int defaultVal = Notification.DEFAULT_LIGHTS;
        if (enabled)
        {
            defaultVal |= Notification.DEFAULT_SOUND;
        }
        customPushNotificationBuilder.setNotificationDefaults(defaultVal);
        PushManager.setNotificationBuilder(context, NOTIFICATION_BUILDER_ID, customPushNotificationBuilder);
    }

    @Override public void setVibrateEnabled(boolean enabled)
    {
        int defaultVal = Notification.DEFAULT_LIGHTS;
        if (enabled)
        {
            defaultVal |= Notification.DEFAULT_VIBRATE;
        }
        customPushNotificationBuilder.setNotificationDefaults(defaultVal);
        PushManager.setNotificationBuilder(context, NOTIFICATION_BUILDER_ID, customPushNotificationBuilder);
    }
}
