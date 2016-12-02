package com.androidth.general.activities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.androidth.general.network.NetworkConstants;
import com.crashlytics.android.Crashlytics;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.utils.Constants;

import java.util.Date;

public class ActivityBuildTypeUtil {

//    private static final String TWITTER_KEY = "j79q8diGnadXdcOFZJ6K13UTL";
//    private static final String TWITTER_SECRET = "TrhCrSePLTF8yCmfsTvU7B3RoOQLgFf2zz0QXJd7KIeJ6WESZ9";

    public static void startCrashReports(@NonNull Context context) {

//        Fabric.with(context, new Crashlytics());//must be initialized before calling below

        // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
//        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(context, new TwitterCore(authConfig));
        //Crashlytics.start(context);

        Crashlytics.getInstance().setString(NetworkConstants.TH_CLIENT_TYPE, String.format("%s:%s", Constants.DEVICE_TYPE, Constants.TAP_STREAM_TYPE.name()));
    }

    public static void setUpCrashReports(@NonNull UserBaseKey currentUserKey)
    {
        Crashlytics.setUserIdentifier("" + currentUserKey.key);
    }

    public static void flagLowMemory()
    {
        Crashlytics.setString("LowMemoryAt", new Date().toString());
    }
}