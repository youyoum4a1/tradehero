package com.tradehero.th.models.push;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.models.push.handlers.PushNotificationHandler;
import dagger.Module;
import dagger.Provides;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;

@Module(
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
        return "noIdSet";
    }

    @Provides @Singleton PushNotificationManager providePushNotificationManager(EmptyPushNotificationManager pushNotificationManager)
    {
        return pushNotificationManager;
    }

    @Provides @Singleton THNotificationBuilder provideTHNotificationBuilder(CommonNotificationBuilder commonNotificationBuilder)
    {
        return commonNotificationBuilder;
    }

    @Provides @Singleton @MaxGroupNotifications IntPreference provideMaxGroupNotifications(@ForUser SharedPreferences sharedPreferences)
    {
        return new IntPreference(sharedPreferences, MAX_GROUP_NOTIFICATIONS, 3);
    }

    // TODO remove classes that ought not to be here
    @Provides(type = Provides.Type.SET_VALUES) @Singleton Set<PushNotificationHandler> providePushNotificationHandler()
    {
        return new HashSet<>();
    }
}
