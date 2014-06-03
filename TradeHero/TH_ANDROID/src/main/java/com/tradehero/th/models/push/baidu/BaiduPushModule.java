package com.tradehero.th.models.push.baidu;

import android.app.Notification;
import android.content.Context;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.tradehero.th.R;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.utils.ForBaiduPush;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
                BaiduPushMessageReceiver.class,
                BaiduIntentReceiver.class,
        },
        // TODO remove static injection
        staticInjections = {
                DeviceTokenHelper.class,
                BaiduPushMessageReceiver.class
        },
        complete = false,
        library = true
)
public class BaiduPushModule
{
    private static final String BAIDU_API_KEY = "iI9WWqP3SfGApTW37UuSyIdc";

    @Provides @Singleton @ForBaiduPush String provideBaiduAppKey()
    {
        return BAIDU_API_KEY;
    }

    @Provides CustomPushNotificationBuilder provideBaiduCustomPushNotificationBuilder(Context context)
    {
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                context.getApplicationContext(),
                R.layout.notification,
                R.id.notification_icon,
                R.id.notification_subject,
                R.id.message
        );

        cBuilder.setNotificationTitle(context.getString(R.string.app_name));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(R.drawable.notification_logo);
        cBuilder.setLayoutDrawable(R.drawable.notification_logo);

        return cBuilder;
    }
}
