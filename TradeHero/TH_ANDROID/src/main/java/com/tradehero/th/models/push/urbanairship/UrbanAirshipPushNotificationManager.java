package com.tradehero.th.models.push.urbanairship;

import com.tradehero.th.base.Application;
import com.tradehero.th.models.push.PushNotificationManager;
import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class UrbanAirshipPushNotificationManager implements PushNotificationManager
{
    private final CustomPushNotificationBuilder customPushNotificationBuilder;

    @Inject public UrbanAirshipPushNotificationManager(CustomPushNotificationBuilder customPushNotificationBuilder)
    {
        this.customPushNotificationBuilder = customPushNotificationBuilder;
    }

    @Override public void initialise()
    {
        UAirship.takeOff(Application.context());

        PushManager.enablePush();
        PushManager.shared().setNotificationBuilder(customPushNotificationBuilder);
        PushManager.shared().setIntentReceiver(UrbanAirshipIntentReceiver.class);

        Timber.d("My Application onCreate - App APID: %s", PushManager.shared().getAPID());
    }

    @Override public void enablePush()
    {
        PushManager.enablePush();
    }

    @Override public void disablePush()
    {
        PushManager.disablePush();
    }

    @Override public void setSoundEnabled(boolean enabled)
    {
        PushManager.shared().getPreferences().setSoundEnabled(enabled);
    }

    @Override public void setVibrateEnabled(boolean enabled)
    {
        PushManager.shared().getPreferences().setVibrateEnabled(enabled);
    }
}
