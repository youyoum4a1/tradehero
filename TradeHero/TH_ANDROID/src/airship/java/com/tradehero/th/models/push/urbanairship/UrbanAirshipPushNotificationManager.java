package com.tradehero.th.models.push.urbanairship;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.base.THApp;
import com.tradehero.th.models.push.PushNotificationManager;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.actions.Action;
import com.urbanairship.actions.ActionArguments;
import com.urbanairship.actions.ActionRegistry;
import com.urbanairship.actions.ActionResult;
import com.urbanairship.google.PlayServicesUtils;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public final class UrbanAirshipPushNotificationManager implements PushNotificationManager
{
    private final AirshipConfigOptions options;
    @Nullable private static UAirship uAirship;

    //<editor-fold desc="Constructors">
    @Inject public UrbanAirshipPushNotificationManager(AirshipConfigOptions options)
    {
        this.options = options;
    }
    //</editor-fold>

    @Nullable public static UAirship getUAirship()
    {
        return uAirship;
    }

    @Override public void initialise()
    {
        UAirship.takeOff(
                THApp.context(),
                options,
                new UAirship.OnReadyCallback()
                {
                    @Override public void onAirshipReady(UAirship uAirship)
                    {
                        UrbanAirshipPushNotificationManager.uAirship = uAirship;
                        THToast.show("My UrbanAirship Application Channel ID: " + uAirship.getPushManager().getChannelId());
                        Timber.i("My UrbanAirship Application Channel ID below");
                        Timber.i("My UrbanAirship Application Channel ID: %s", uAirship.getPushManager().getChannelId());
                        if (BuildConfig.DEBUG)
                        {
                            uAirship.getPushManager().setUserNotificationsEnabled(true);
                        }
                    }
                });

        ActionRegistry.shared().registerAction(new Action()
        {
            @Override public ActionResult perform(String s, ActionArguments actionArguments)
            {
                Timber.e(new Exception("Reporting"), "s is %s, argument are %s", s, actionArguments);
                return ActionResult.newEmptyResult();
            }
        },
        "TODO");
    }

    @Override public void verify(@NonNull Activity activity)
    {
        // Handle any Google Play Services errors
        if (PlayServicesUtils.isGooglePlayStoreAvailable())
        {
            PlayServicesUtils.handleAnyPlayServicesError(activity);
        }
    }

    @Override public void enablePush()
    {
        UAirship.shared().getPushManager().setPushEnabled(true);
    }

    @Override public void disablePush()
    {
        UAirship.shared().getPushManager().setPushEnabled(false);
    }

    @Override public void setSoundEnabled(boolean enabled)
    {
        UAirship.shared().getPushManager().setSoundEnabled(enabled);
    }

    @Override public void setVibrateEnabled(boolean enabled)
    {
        UAirship.shared().getPushManager().setVibrateEnabled(enabled);
    }
}
