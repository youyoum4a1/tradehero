package com.tradehero.th.models.push.urbanairship;

import com.tradehero.th.models.push.handlers.GcmDeletedHandler;
import com.tradehero.th.models.push.handlers.NotificationOpenedHandler;
import com.tradehero.th.models.push.handlers.PushNotificationHandler;
import com.tradehero.th.models.push.handlers.PushReceivedHandler;
import com.tradehero.th.models.push.handlers.RegistrationFinishedHandler;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;

@Module(
        injects = {
                UrbanAirshipIntentReceiver.class
        },
        complete = false,
        library = true
)
public class UrbanAirshipPushModule
{
    @Provides(type = Provides.Type.SET_VALUES) @Singleton Set<PushNotificationHandler> provideUrbanAirshipPushNotificationHandler(
            NotificationOpenedHandler notificationOpenedHandler,
            PushReceivedHandler pushReceivedHandler,
            GcmDeletedHandler gcmDeletedHandler,
            RegistrationFinishedHandler registrationFinishedHandler
    )
    {
        return new HashSet<>(Arrays.asList(new PushNotificationHandler[] {
                notificationOpenedHandler,
                pushReceivedHandler,
                gcmDeletedHandler,
                registrationFinishedHandler
        }));
    }
}
