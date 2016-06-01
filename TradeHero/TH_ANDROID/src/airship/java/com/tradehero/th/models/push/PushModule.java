package com.ayondo.academy.models.push;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.ayondo.academy.models.push.urbanairship.UrbanAirshipPushModule;
import com.ayondo.academy.models.push.urbanairship.UrbanAirshipPushNotificationManager;
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
