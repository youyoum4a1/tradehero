package com.tradehero.th.models.push;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.models.push.baidu.BaiduPushManager;
import com.tradehero.th.models.push.baidu.BaiduPushModule;
import com.tradehero.th.models.push.urbanairship.UrbanAirshipPushModule;
import com.tradehero.th.models.push.urbanairship.UrbanAirshipPushNotificationManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Provider;
import javax.inject.Singleton;
import timber.log.Timber;

@Module(
        includes = {
                BaiduPushModule.class,
                UrbanAirshipPushModule.class,
        },
        injects = {
                DefaultIntentReceiver.class,
        },
        complete = false,
        library = true
)
public class PushModule
{
    private static final String MAX_GROUP_NOTIFICATIONS = "MAX_GROUP_NOTIFICATIONS";

    @Provides @Singleton PushNotificationManager providePushNotificationManager(
            Provider<BaiduPushManager> baiduPushManager,
            Provider<UrbanAirshipPushNotificationManager> urbanAirshipPushNotificationManager)
    {
        boolean isChineseVersion = DeviceTokenHelper.isChineseVersion();
        if (isChineseVersion)
        {
            Timber.d("Using Baidu Push");
            return baiduPushManager.get();
        }
        else
        {
            Timber.d("Using UrbanAirship Push");
            return urbanAirshipPushNotificationManager.get();
        }
    }

    @Provides @Singleton THNotificationBuilder provideTHNotificationBuilder(CommonNotificationBuilder commonNotificationBuilder)
    {
        return commonNotificationBuilder;
    }

    @Provides @Singleton @MaxGroupNotifications IntPreference provideMaxGroupNotifications(SharedPreferences sharedPreferences)
    {
        return new IntPreference(sharedPreferences, MAX_GROUP_NOTIFICATIONS, 3);
    }
}
