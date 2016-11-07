package com.androidth.general.utils.metrics;

import android.content.Context;

import com.tapstream.sdk.Config;
import com.tapstream.sdk.Tapstream;
import com.androidth.general.base.THApp;
import com.androidth.general.utils.Constants;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.androidth.general.utils.Constants.DOGFOOD_BUILD;
import static com.androidth.general.utils.Constants.LOCALYTICS_APP_KEY_DEBUG;
import static com.androidth.general.utils.Constants.LOCALYTICS_APP_KEY_RELEASE;
import static com.androidth.general.utils.Constants.RELEASE;

@Module(
        library = true,
        complete = false
)
public class MetricsModule
{
    private static final String LOCALYTICS_KEY =
            RELEASE ? (DOGFOOD_BUILD ? LOCALYTICS_APP_KEY_DEBUG : LOCALYTICS_APP_KEY_RELEASE) : null;
    public static final boolean LOCALYTICS_PUSH_ENABLED = RELEASE;
    public static final String APP_FLYER_KEY = "pEuxjZE2GpyRXXwFjHHRRU";

    @Deprecated
    public static final String TD_APP_ID_KEY = RELEASE ? "5991FF8EFB8EFF717C206FCCF9C969A8" : null;

    @Provides(type = Provides.Type.SET_VALUES) @ForAnalytics Set<String> provideAnalyticsPredefineDimensions()
    {
        Set<String> predefinedDimensions = new HashSet<>();
        predefinedDimensions.add(Constants.TAP_STREAM_TYPE.name());
        return predefinedDimensions;
    }

    @Provides @Singleton public Void provideAnalytics(Context context)
    {
        //TODO Change Analytics
        //return Analytics.with(THApp.get(context)).withLocalytics(LOCALYTICS_KEY).withTalkingData(TD_APP_ID_KEY, Constants.TAP_STREAM_TYPE.name()).build();
        return null;
    }

//    // TapStream
//    @Provides @Singleton Tapstream provideTapStream(THApp app, Config config)
//    {
//        Tapstream.create(app, config);
//        return Tapstream.getInstance();
//    }

//    @Provides @Singleton Config provideTapStreamConfig(Context context)
//    {
//        Config config = new Config(TAPSTREAM_APP_NAME, TAPSTREAM_KEY);
//        config.setFireAutomaticOpenEvent(false);//this will send twice
//        config.setInstallEventName(context.getString(Constants.TAP_STREAM_TYPE.installResId));
//        return config;
//    }

//    // MobileAppTracker
//    @Provides @Singleton Tune provideMobileAppTracker(Context context)
//    {
//        Tune mobileAppTrackerInstance = Tune.getInstance();
//        mobileAppTrackerInstance.setPackageName(context.getPackageName() + "." + Constants.TAP_STREAM_TYPE.name());
//        mobileAppTrackerInstance.setDebugMode(!Constants.RELEASE);
//
////        MobileAppTracker.init(context, MAT_APP_ID, MAT_APP_KEY);
////        MobileAppTracker mobileAppTrackerInstance = MobileAppTracker.getInstance();
////        mobileAppTrackerInstance.setPackageName(context.getPackageName() + "." + Constants.TAP_STREAM_TYPE.name());
////        mobileAppTrackerInstance.setDebugMode(!Constants.RELEASE);
//        return mobileAppTrackerInstance;
//    }
}
