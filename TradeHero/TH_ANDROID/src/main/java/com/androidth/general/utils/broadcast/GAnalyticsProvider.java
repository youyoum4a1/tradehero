package com.androidth.general.utils.broadcast;

import android.app.Activity;

import com.androidth.general.base.THApp;
import com.google.android.gms.analytics.HitBuilders;

import java.util.Map;

/**
 * Created by jeffgan on 21/9/16.
 */

public class GAnalyticsProvider {

    /**
     * COMP - Prefix for competition
     * LOCAL_ Prefix for outside competition
     */

    public final static String COMP_MAIN_PAGE = "Competition Main Page";
    public final static String COMP_JOIN_PAGE = "Join Competition Page";
    public final static String COMP_KYC_1 = "KYC 1";
    public final static String COMP_KYC_2 = "KYC 2";
    public final static String COMP_KYC_3 = "KYC 3";
    public final static String COMP_KYC_4 = "KYC 4";
    public final static String COMP_KYC_5 = "KYC 5";
    public final static String LOCAL_TRENDING_SCREEN = "Trending Screen";
    public final static String COMP_PORT_OPEN = "Competition Portfolio / Open";
    public final static String COMP_PORT_CLOSE = "Competition Portfolio / Close";
    public final static String COMP_TAB_MOST_ACTIVE = "Most Active Screen";
    public final static String COMP_TAB_TOP_GAINER = "Top Gainer Screen";
    public final static String COMP_TAB_TOP_LOSER = "Top Loser Screen";
    public final static String LOCAL_PROFILE_SCREEN = "Profile Screen";
    public final static String LOCAL_DISCOVER_NEWSFEED = "Discover/Newsfeed Screen";
    public final static String LOCAL_DISCOVER_DISCUSSIONS = "Discover/Discussions Screen";
    public final static String LOCAL_DISCOVER_ACADEMY = "Discover/Academy Screen";
    public final static String LOCAL_FOLLOW_STOCKS_HEROES = "Follow Stocks Heroes";
    public final static String COMP_TRADE_NOW = "Trade Now! Competition Screen";
    public final static String LOCAL_TRADE_NOW = "Trade Now! Screen";
    public final static String COMP_BUY_SELL = "Buy/Sell Competition Screen";
    public final static String LOCAL_BUY_SELL = "Buy/Sell Screen";
    public final static String LOCAL_FX_BUY_SELL = "Buy/Sell FX Screen";
    public final static String COMP_BUY_NOW = "Buy Now! Competition Screen";
    public final static String LOCAL_BUY_NOW = "Buy Now! Screen";
    public final static String LOCAL_FX_BUY_NOW = "Buy Now! FX Screen";
    public final static String COMP_SELL_NOW = "Sell Now! Competition Screen";
    public final static String LOCAL_SELL_NOW = "Sell Now! Screen";
    public final static String LOCAL_FX_SELL_NOW = "Sell Now! FX Screen";


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
