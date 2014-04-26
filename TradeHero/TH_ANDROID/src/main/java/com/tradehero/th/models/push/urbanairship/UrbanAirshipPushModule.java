package com.tradehero.th.models.push.urbanairship;

import com.tradehero.th.models.push.urbanairship.handlers.GcmDeletedHandler;
import com.tradehero.th.models.push.urbanairship.handlers.NotificationOpenedHandler;
import com.tradehero.th.models.push.urbanairship.handlers.PushNotificationHandler;
import com.tradehero.th.models.push.urbanairship.handlers.PushReceivedHandler;
import com.tradehero.th.models.push.urbanairship.handlers.RegistrationFinishedHandler;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;

/**
 * Created by thonguyen on 26/4/14.
 */
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
