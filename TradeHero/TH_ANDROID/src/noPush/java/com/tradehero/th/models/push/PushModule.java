package com.androidth.general.models.push;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import com.androidth.general.common.annotation.ForUser;
import com.androidth.general.common.persistence.prefs.IntPreference;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class PushModule
{
    private static final String MAX_GROUP_NOTIFICATIONS = "MAX_GROUP_NOTIFICATIONS";

    @Nullable public static String getPushId()
    {
        return "noIdSet";
    }

    @Provides @Singleton PushNotificationManager providePushNotificationManager(EmptyPushNotificationManager pushNotificationManager)
    {
        return pushNotificationManager;
    }

    @Provides @Singleton @MaxGroupNotifications IntPreference provideMaxGroupNotifications(@ForUser SharedPreferences sharedPreferences)
    {
        return new IntPreference(sharedPreferences, MAX_GROUP_NOTIFICATIONS, 3);
    }
}
