package com.tradehero.th.models.push.urbanairship;

import android.support.annotation.NonNull;
import com.tradehero.th.base.THApp;
import com.tradehero.th.models.push.PushNotificationManager;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.google.PlayServicesUtils;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.notifications.NotificationFactory;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import timber.log.Timber;

@Singleton public final class UrbanAirshipPushNotificationManager implements PushNotificationManager
{
    @NonNull private final AirshipConfigOptions options;
    @NonNull private final Lazy<NotificationFactory> notificationFactory;

    @Inject public UrbanAirshipPushNotificationManager(
            @NonNull AirshipConfigOptions options,
            @NonNull Lazy<NotificationFactory> notificationFactory)
    {
        this.options = options;
        this.notificationFactory = notificationFactory;
    }

    @Override public void initialise()
    {
        UAirship.takeOff(THApp.context(), options);

        String channelId = UAirship.shared().getPushManager().getChannelId();
        Timber.i("UrbanAirship Channel Id: %s", channelId);

        // Handle any Google Play Services errors
        if (PlayServicesUtils.isGooglePlayStoreAvailable())
        {
            PlayServicesUtils.handleAnyPlayServicesError(THApp.context());
        }

        // TODO register specific com.urbanairship.actions.Action if necessary
        Observable.create(new UrbanAirshipOnReadyOperator(THApp.context()))
                .subscribe(
                        airship -> this.registerActions(),
                        throwable -> Timber.e(throwable, "UrbanAirship cannot get ready"));

        UAirship.shared().getPushManager().setNotificationFactory(notificationFactory.get());
    }

    public void registerActions()
    {
        //ActionRegistry.shared().registerAction(new Action(){}, "my_custom_action");
    }

    @Override public void enablePush()
    {
        UAirship.shared().getPushManager().setUserNotificationsEnabled(true);
    }

    @Override public void disablePush()
    {
        UAirship.shared().getPushManager().setUserNotificationsEnabled(false);
    }

    @Override public void setSoundEnabled(boolean enabled)
    {
        PushManager.shared().setSoundEnabled(enabled);
    }

    @Override public void setVibrateEnabled(boolean enabled)
    {
        PushManager.shared().setVibrateEnabled(enabled);
    }
}
