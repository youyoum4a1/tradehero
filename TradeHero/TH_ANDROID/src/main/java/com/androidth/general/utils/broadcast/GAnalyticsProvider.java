package com.androidth.general.utils.broadcast;

import android.app.Activity;

import com.androidth.general.base.THApp;
import com.google.android.gms.analytics.HitBuilders;

import java.util.Map;

/**
 * Created by jeffgan on 21/9/16.
 */

public class GAnalyticsProvider {

    public final static String COMP_MAIN_PAGE = "Competition Main Page";
    public final static String JOIN_COMP_PAGE = "Join Competition Page";
    public final static String KYC_1 = "KYC 1";
    public final static String KYC_2 = "KYC 2";
    public final static String KYC_3 = "KYC 3";
    public final static String KYC_4 = "KYC 4";
    public final static String KYC_5 = "KYC 5";
    public final static String TRENDING_SCREEN = "Trending Screen";
    public final static String COMP_PORT_OPEN = "Competition Portfolio / Open";
    public final static String COMP_PORT_CLOSE = "Competition Portfolio / Close";

    public static void sendGAEvents(Activity activity, Map<String, String> eventDetails){
        THApp application = (THApp) activity.getApplication();
//        application.getDefaultTracker().send(eventDetails);
        application.getTracker(THApp.TrackerName.APP_TRACKER).send(eventDetails);
    }

    public static void sendGAScreen(Activity activity, String activityName){

        THApp application = (THApp) activity.getApplication();
        application.getTracker(THApp.TrackerName.APP_TRACKER).setScreenName(activityName);
        application.getTracker(THApp.TrackerName.APP_TRACKER).send(new HitBuilders.ScreenViewBuilder().build());//for screen info
    }
}
