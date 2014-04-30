package com.tradehero.th.models.push.baidu;

import android.app.Notification;
import android.content.Context;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushNotificationBuilder;
import com.baidu.android.pushservice.PushSettings;
import com.baidu.frontia.FrontiaApplication;
import com.tradehero.th.R;
import com.tradehero.th.models.push.urbanairship.UrbanAirshipPushNotificationManager;
import com.tradehero.th.utils.ForBaiduPush;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class BaiduPushManager extends UrbanAirshipPushNotificationManager
{
    private final Context context;
    private final String appKey;

    @Inject public BaiduPushManager(Context context, @ForBaiduPush String appKey)
    {
        this.context = context;
        this.appKey = appKey;
    }

    @Override public void initialise()
    {
        super.initialise();

        Timber.d("initialise(FrontiaApplication.initFrontiaApplication) context:%s", context);
        FrontiaApplication.initFrontiaApplication(context);
    }

    @Override public void enablePush()
    {
        super.enablePush();

        Timber.d("enablePush(PushManager.startWork) context:%s, appKey:%s", context, appKey);
        PushSettings.enableDebugMode(context, true);
        PushManager.setNotificationBuilder(context, BaiduPushMessageReceiver.MESSAGE_ID,
                createDefaultNotificationBuilder());
        //PushManager.disableLbs(context);
        PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY, appKey);
    }

    @Override public void disablePush()
    {
        super.disablePush();

        PushManager.stopWork(context);
    }

    @Override public void setSoundEnabled(boolean enabled)
    {
        super.setSoundEnabled(enabled);

        boolean isVibrateEnabled =
                com.urbanairship.push.PushManager.shared().getPreferences().isVibrateEnabled();
        int defaultVal = Notification.DEFAULT_LIGHTS;
        if (isVibrateEnabled)
        {
            defaultVal |= Notification.DEFAULT_VIBRATE;
        }
        if (enabled)
        {
            defaultVal |= Notification.DEFAULT_SOUND;
        }
        PushNotificationBuilder builder = createDefaultNotificationBuilder();
        builder.setNotificationDefaults(defaultVal);
        PushManager.setNotificationBuilder(context, BaiduPushMessageReceiver.MESSAGE_ID, builder);
    }

    @Override public void setVibrateEnabled(boolean enabled)
    {
        super.setVibrateEnabled(enabled);

        boolean isSoundEnabled =
                com.urbanairship.push.PushManager.shared().getPreferences().isSoundEnabled();
        int defaultVal = Notification.DEFAULT_LIGHTS;
        if (enabled)
        {
            defaultVal |= Notification.DEFAULT_VIBRATE;
        }
        if (isSoundEnabled)
        {
            defaultVal |= Notification.DEFAULT_SOUND;
        }
        PushNotificationBuilder builder = createDefaultNotificationBuilder();
        builder.setNotificationDefaults(defaultVal);
        PushManager.setNotificationBuilder(context, BaiduPushMessageReceiver.MESSAGE_ID, builder);
    }

    private PushNotificationBuilder createDefaultNotificationBuilder()
    {
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
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

        return cBuilder;
    }
}
