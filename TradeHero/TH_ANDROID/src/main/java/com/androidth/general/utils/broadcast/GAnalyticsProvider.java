package com.androidth.general.utils.broadcast;

import android.app.Activity;

import com.androidth.general.base.THApp;
import com.google.android.gms.analytics.HitBuilders;

import java.util.Map;

/**
 * Created by jeffgan on 21/9/16.
 */

public class GAnalyticsProvider {

    public static void sendGAEvents(Activity activity, String activityName, Map<String, String> eventDetails){
        THApp application = (THApp) activity.getApplication();
        application.getDefaultTracker().setScreenName(activityName);
//        application.getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());//for screen info
        application.getDefaultTracker().send(eventDetails);
    }
}
