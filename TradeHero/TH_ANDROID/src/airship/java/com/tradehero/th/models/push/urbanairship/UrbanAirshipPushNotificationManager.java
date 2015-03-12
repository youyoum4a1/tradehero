package com.tradehero.th.models.push.urbanairship;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THApp;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.rx.ReplaceWith;
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
    @NonNull private final SessionServiceWrapper sessionServiceWrapper;
    @NonNull private final StringPreference savedPushDeviceIdentifier;
    @Nullable private static UAirship uAirship;

    //<editor-fold desc="Constructors">
    @Inject public UrbanAirshipPushNotificationManager(@NonNull AirshipConfigOptions options,
            @NonNull SessionServiceWrapper sessionServiceWrapper,
            @NonNull @SavedPushDeviceIdentifier StringPreference savedPushDeviceIdentifier)
    {
        this.options = options;
        this.sessionServiceWrapper = sessionServiceWrapper;
        this.savedPushDeviceIdentifier = savedPushDeviceIdentifier;
    }
    //</editor-fold>

    @Nullable public static UAirship getUAirship()
    {
        return uAirship;
    }

    @NonNull @Override public Observable<PushNotificationManager.InitialisationCompleteDTO> initialise()
    {
        final long before = System.nanoTime();
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
                .flatMap(new Func1<UAirship, Observable<PushNotificationManager.InitialisationCompleteDTO>>()
                {
                    @Override public Observable<PushNotificationManager.InitialisationCompleteDTO> call(UAirship uAirship)
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

                        registerActions();

                        InitialisationCompleteDTO initialisationCompleteDTO = new InitialisationCompleteDTO(channelId, uAirship);
                        Timber.d("UrbanAirship Initialisation %d milliseconds", (System.nanoTime() - before) / 1000000);
                        savedPushDeviceIdentifier.set(channelId);
                        return sessionServiceWrapper.updateDeviceRx()
                                .map(new ReplaceWith<UserProfileDTO, PushNotificationManager.InitialisationCompleteDTO>(initialisationCompleteDTO));
                    }
                });
    }

    private void registerActions()
    {
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

    public static class InitialisationCompleteDTO extends PushNotificationManager.InitialisationCompleteDTO
    {
        @NonNull public final UAirship uAirship;

        public InitialisationCompleteDTO(@NonNull String pushId, @NonNull UAirship uAirship)
        {
            super(pushId);
            this.uAirship = uAirship;
        }
    }
}
