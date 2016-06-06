package com.androidth.general.models.push;

import android.content.SharedPreferences;
import com.androidth.general.common.annotation.ForUser;
import com.androidth.general.common.persistence.prefs.IntPreference;
import com.androidth.general.models.push.urbanairship.UrbanAirshipPushModule;
import com.androidth.general.models.push.urbanairship.UrbanAirshipPushNotificationManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
                UrbanAirshipPushModule.class,
        },
        injects = {
        },
        complete = false,
        library = true
)
public class PushModule
{
    private static final String MAX_GROUP_NOTIFICATIONS = "MAX_GROUP_NOTIFICATIONS";

    @Provides @Singleton PushNotificationManager providePushNotificationManager(UrbanAirshipPushNotificationManager urbanAirshipPushNotificationManager)
    {
        return urbanAirshipPushNotificationManager;
    }

    @Provides @Singleton @MaxGroupNotifications IntPreference provideMaxGroupNotifications(@ForUser SharedPreferences sharedPreferences)
    {
        return new IntPreference(sharedPreferences, MAX_GROUP_NOTIFICATIONS, 3);
    }
}
