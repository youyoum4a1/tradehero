package com.tradehero.th.models.push;

import android.content.SharedPreferences;

import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.models.push.baidu.BaiduPushManager;
import com.tradehero.th.models.push.baidu.BaiduPushModule;
import com.tradehero.th.utils.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module(
        includes = {
                BaiduPushModule.class
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
            BaiduPushManager baiduPushManager)
    {
        switch (Constants.TAP_STREAM_TYPE.pushProvider)
        {
            case BAIDU:
                Timber.d("Using Baidu Push");
                return baiduPushManager;

            default:
                throw new IllegalArgumentException("Unhandled PushProvider." + Constants.TAP_STREAM_TYPE.pushProvider.name());
        }
    }

    @Provides @Singleton THNotificationBuilder provideTHNotificationBuilder(CommonNotificationBuilder commonNotificationBuilder)
    {
        return commonNotificationBuilder;
    }

    @Provides @Singleton @MaxGroupNotifications IntPreference provideMaxGroupNotifications(@ForUser SharedPreferences sharedPreferences)
    {
        return new IntPreference(sharedPreferences, MAX_GROUP_NOTIFICATIONS, 3);
    }
}
