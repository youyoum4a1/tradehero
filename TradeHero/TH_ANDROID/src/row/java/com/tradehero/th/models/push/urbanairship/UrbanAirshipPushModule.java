package com.tradehero.th.models.push.urbanairship;

import android.app.Notification;
import android.content.Context;
import com.tradehero.common.annotation.Temp;
import com.tradehero.th.R;
import com.tradehero.th.models.push.handlers.GcmDeletedHandler;
import com.tradehero.th.models.push.handlers.NotificationOpenedHandler;
import com.tradehero.th.models.push.handlers.PushNotificationHandler;
import com.tradehero.th.models.push.handlers.PushReceivedHandler;
import com.tradehero.th.models.push.handlers.RegistrationFinishedHandler;
import com.tradehero.th.utils.Constants;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.push.notifications.CustomLayoutNotificationFactory;
import com.urbanairship.push.notifications.NotificationFactory;
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

    @Provides @Temp NotificationFactory provideNotificationFactory(THNotificationFactory notificationFactory)
    {
        return notificationFactory;
    }

    @Provides AirshipConfigOptions provideAirshipConfigOptions(Context context)
    {
        AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(context);
        if (Constants.DOGFOOD_BUILD)
        {
            options.inProduction = false;
            options.gcmSender = Constants.GCM_STAGING_SENDER;
        }
        return options;
    }

    @Provides NotificationFactory provideCustomPushNotificationBuilder(Context context)
    {
        CustomLayoutNotificationFactory nb = new CustomLayoutNotificationFactory(context);

        nb.statusBarIconDrawableId = R.drawable.th_logo;

        nb.layout = R.layout.notification;
        nb.layoutIconDrawableId = R.drawable.notification_logo;
        nb.layoutIconId = R.id.notification_icon;
        nb.layoutSubjectId = R.id.notification_subject;
        nb.layoutMessageId = R.id.message;
        return nb;
    }
}
