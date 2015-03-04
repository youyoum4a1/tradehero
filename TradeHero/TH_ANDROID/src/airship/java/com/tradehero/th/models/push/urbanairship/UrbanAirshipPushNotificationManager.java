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
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

@Singleton public final class UrbanAirshipPushNotificationManager implements PushNotificationManager
{
    @NonNull private final AirshipConfigOptions options;
    @Nullable private static UAirship uAirship;

    //<editor-fold desc="Constructors">
    @Inject public UrbanAirshipPushNotificationManager(@NonNull AirshipConfigOptions options)
    {
        this.options = options;
    }
    //</editor-fold>

    @Nullable public static UAirship getUAirship()
    {
        return uAirship;
    }

    @NonNull @Override public Observable<InitialisationCompleteDTO> initialise()
    {
        return Observable.create(
                new Observable.OnSubscribe<UAirship>()
                {
                    @Override public void call(final Subscriber<? super UAirship> subscriber)
                    {
                        UAirship.takeOff(
                                THApp.context(),
                                options,
                                new UAirship.OnReadyCallback()
                                {
                                    @Override public void onAirshipReady(UAirship uAirship)
                                    {
                                        subscriber.onNext(uAirship);
                                    }
                                });
                    }
                })
                .map(new Func1<UAirship, InitialisationCompleteDTO>()
                {
                    @Override public InitialisationCompleteDTO call(UAirship uAirship)
                    {
                        final String channelId = uAirship.getPushManager().getChannelId();
                        UrbanAirshipPushNotificationManager.uAirship = uAirship;
                        uAirship.getPushManager().setDeviceTagsEnabled(false);
                        THToast.show("My UrbanAirship Application Channel ID: " + channelId);
                        Timber.i("My UrbanAirship Application Channel ID below");
                        Timber.i("My UrbanAirship Application Channel ID: %s", channelId);
                        if (BuildConfig.DEBUG)
                        {
                            uAirship.getPushManager().setUserNotificationsEnabled(true);
                        }

                        ActionRegistry.shared().registerAction(
                                new Action()
                                {
                                    @Override public ActionResult perform(String s, ActionArguments actionArguments)
                                    {
                                        Timber.e(new Exception("Reporting"), "s is %s, argument are %s", s,
                                                actionArguments);
                                        return ActionResult.newEmptyResult();
                                    }
                                },
                                "TODO");

                        return new InitialisationComplete(channelId, uAirship);
                    }
                });
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

    public static class InitialisationComplete extends PushNotificationManager.InitialisationCompleteDTO
    {
        @NonNull public final UAirship uAirship;

        public InitialisationComplete(@NonNull String pushId, @NonNull UAirship uAirship)
        {
            super(pushId);
            this.uAirship = uAirship;
        }
    }
}
