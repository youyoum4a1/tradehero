package com.tradehero.th.models.push.urbanairship;

import com.tradehero.th.THApp;
import com.tradehero.th.models.push.PushNotificationManager;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushNotificationBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import timber.log.Timber;

@Singleton public final class UrbanAirshipPushNotificationManager implements PushNotificationManager
{
    private final Lazy<PushNotificationBuilder> customPushNotificationBuilder;
    private final AirshipConfigOptions options;

    @Inject public UrbanAirshipPushNotificationManager(
            Lazy<PushNotificationBuilder> customPushNotificationBuilder,
            AirshipConfigOptions options)
    {
        this.customPushNotificationBuilder = customPushNotificationBuilder;
        this.options = options;
    }

    @Override public void initialise()
    {
        UAirship.takeOff(THApp.context(), options);

        PushManager.enablePush();
        PushManager.shared().setNotificationBuilder(customPushNotificationBuilder.get());
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
