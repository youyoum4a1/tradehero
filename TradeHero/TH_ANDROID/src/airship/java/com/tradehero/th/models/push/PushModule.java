package com.tradehero.th.models.push;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.models.push.urbanairship.UrbanAirshipPushModule;
import com.tradehero.th.models.push.urbanairship.UrbanAirshipPushNotificationManager;
import com.urbanairship.UAirship;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
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

    @Nullable public static String getPushId()
    {
        UAirship uAirship = UrbanAirshipPushNotificationManager.getUAirship();
        if (uAirship == null)
        {
            return null;
        }
        return uAirship.getPushManager().getChannelId();
    }

    @Provides @Singleton PushNotificationManager providePushNotificationManager(UrbanAirshipPushNotificationManager urbanAirshipPushNotificationManager)
    {
        return urbanAirshipPushNotificationManager;
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
