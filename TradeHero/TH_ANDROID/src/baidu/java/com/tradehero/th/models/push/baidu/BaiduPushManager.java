package com.tradehero.th.models.push.baidu;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.baidu.frontia.FrontiaApplication;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.utils.ForBaiduPush;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import timber.log.Timber;

@Singleton public final class BaiduPushManager implements PushNotificationManager
{
    private static final int NOTIFICATION_BUILDER_ID = 1;

    private final Context context;
    //private final Lazy<CustomPushNotificationBuilder> customPushNotificationBuilder;
    private final String appKey;

    @Inject public BaiduPushManager(
            Context context,
            //Lazy<CustomPushNotificationBuilder> customPushNotificationBuilder,
            @ForBaiduPush String appKey)
    {
        this.context = context;
        //this.customPushNotificationBuilder = customPushNotificationBuilder;
        this.appKey = appKey;
    }

    @NonNull @Override public Observable<InitialisationCompleteDTO> initialise()
    {
        try
        {
            FrontiaApplication.initFrontiaApplication(context);
        } catch (Throwable e)
        {
            return Observable.error(e);
        }
        //TODO need check whether this is ok for urbanship,
        //TODO for baidu, PushManager.startWork can't run in Application.init() for stability, it will run in a circle. by alex
        //enablePush();
        return Observable.just(new InitialisationCompleteDTO("fake"));
    }

    @Override public void verify(@NonNull Activity activity)
    {
        // Nothing to do
    }

    @Override public void enablePush()
    {
        Timber.d("enablePush(PushManager.startWork) context:%s, appKey:%s", context, appKey);
        PushSettings.enableDebugMode(context, true);
        //PushManager.setNotificationBuilder(context, NOTIFICATION_BUILDER_ID, customPushNotificationBuilder.get());
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
        //customPushNotificationBuilder.get().setNotificationDefaults(defaultVal);
        //PushManager.setNotificationBuilder(context, NOTIFICATION_BUILDER_ID, customPushNotificationBuilder.get());
    }

    @Override public void setVibrateEnabled(boolean enabled)
    {
        int defaultVal = Notification.DEFAULT_LIGHTS;
        if (enabled)
        {
            defaultVal |= Notification.DEFAULT_VIBRATE;
        }
        //customPushNotificationBuilder.get().setNotificationDefaults(defaultVal);
        //PushManager.setNotificationBuilder(context, NOTIFICATION_BUILDER_ID, customPushNotificationBuilder.get());
    }
}
