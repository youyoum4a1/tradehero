package com.androidth.general.utils.broadcast;

import android.app.Activity;

import com.androidth.general.base.THApp;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.google.android.gms.analytics.HitBuilders;

import java.util.HashMap;
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
    public final static String COMP_TRADE_NOW = "Competition Trade Now! Screen";
    public final static String LOCAL_TRADE_NOW = "Trade Now! Screen";
    public final static String COMP_BUY_SELL = "Competition Buy/Sell Screen";
    public final static String LOCAL_BUY_SELL = "Buy/Sell Screen";
    public final static String LOCAL_FX_BUY_SELL = "Buy/Sell FX Screen";
    public final static String COMP_BUY_NOW = "Competition Buy Now! Screen";
    public final static String LOCAL_BUY_NOW = "Buy Now! Screen";
    public final static String LOCAL_FX_BUY_NOW = "Buy Now! FX Screen";
    public final static String COMP_SELL_NOW = "Competition Sell Now! Screen";
    public final static String LOCAL_SELL_NOW = "Sell Now! Screen";
    public final static String LOCAL_FX_SELL_NOW = "Sell Now! FX Screen";

    public final static String CATEGORY = "Category";
    public final static String ACTION = "Action";
    public final static String LABEL = "LABEL";
    public final static String ACTION_POPUP_SCREEN = "Pop Up Screen";
    public final static String ACTION_CLICK_TRENDING = "Click Trending Page";
    public final static String ACTION_TAB_COMP = "Tab Competition Bar";
    public final static String ACTION_CLICK_COMP_BANNER = "Click Competition Banner";
    public final static String ACTION_KYC_1_NEXT = "KYC 1 Next";
    public final static String ACTION_KYC_2_NEXT = "KYC 2 Next";
    public final static String ACTION_KYC_3_NEXT = "KYC 3 Next";
    public final static String ACTION_KYC_4_NEXT = "KYC 4 Next";
    public final static String ACTION_KYC_5_NEXT = "KYC 5 Next";
    public final static String ACTION_ENTER_COMP_PORTFOLIO = "Enter Competition Portfolio";
    public final static String ACTION_ENTER_OPEN = "Enter Open";
    public final static String ACTION_ENTER_OPEN_SLIDE_TO_MORE = "Enter Open / Slide Shares To More";
    public final static String ACTION_ENTER_OPEN_ENTER_SHARES = "Enter Open / Slide Shares To More / Buy & Sell";
    public final static String ACTION_ENTER_CLOSE = "Enter Close";
    public final static String ACTION_ENTER_CLOSE_SLIDE_TO_MORE = "Enter Close / Slide Shares To More";
    public final static String ACTION_ENTER_CLOSE_ENTER_SHARES = "Enter CLOSE / Slide Shares To More / Buy & Sell";
    public final static String ACTION_ENTER_OPEN_ENTER_TRADE = "Enter Open / Enter Shares / Enter TRADE NOW";
    public final static String ACTION_ENTER_CLOSE_ENTER_TRADE = "Enter Close / Enter Shares / Enter TRADE NOW";
    public final static String ACTION_ENTER_BUY = "Enter Buy";
    public final static String ACTION_ENTER_SELL = "Enter Sell";

    public final static String ACTION_ENTER_BUY_MOVE_SLIDER = "Enter Buy / Move Slider";
    public final static String ACTION_ENTER_BUY_BUY_NOW = "Enter Buy / Enter BUY NOW page";
    public final static String ACTION_ENTER_BUY_SHARE = "Enter Buy/Sell / Share on Social Media";//Not fully logical
    public final static String ACTION_ENTER_BUY_INPUT = "Enter Buy / Input Figures";
    public final static String ACTION_ENTER_BUY_COMMENT = "Enter Buy / Add a Comment";

    public final static String ACTION_ENTER_SELL_MOVE_SLIDER = "Enter Sell / Move Slider";
    public final static String ACTION_ENTER_SELL_SELL_NOW = "Enter Sell / Enter SELL NOW page";
    public final static String ACTION_ENTER_SELL_SHARE = "Enter Sell / Share on Social Media";
    public final static String ACTION_ENTER_SELL_INPUT = "Enter Sell / Input Figures";
    public final static String ACTION_ENTER_SELL_COMMENT = "Enter Sell / Add a Comment";

    public final static String ACTION_ADD_TO_FAVE = "Click ADD TO FAVOURITE";
    public final static String ACTION_ADD_ALERT = "Select STOCK ALERT";
    public final static String ACTION_BUY_SELL_SCREEN = "Buy / Sell Screen";

    private static void sendGAEvents(Map<String, String> eventDetails){
        try{
//            THApp application = (THApp) activity.getApplication();
//        application.getDefaultTracker().send(eventDetails);

            Map<String, String> events = new HitBuilders.EventBuilder()
                    .setCategory(eventDetails.get(CATEGORY)!=null? eventDetails.get(CATEGORY):"Default Category")
                    .setAction(eventDetails.get(ACTION)!=null? eventDetails.get(ACTION):"Default Action")
                    .setLabel(eventDetails.get(LABEL)!=null? eventDetails.get(LABEL):"")
                    .build();
            THApp.getTracker(THApp.TrackerName.APP_TRACKER).send(events);

        }catch (Exception e){
            //activity might be null
            new TimberOnErrorAction1(e.getMessage());
        }

    }

    public static void sendGAScreenEvent(Activity activity, String activityName){
//TODO change this, remove activity
        try{
            THApp application = (THApp) activity.getApplication();
            THApp.getTracker(THApp.TrackerName.APP_TRACKER).setScreenName(activityName);
            THApp.getTracker(THApp.TrackerName.APP_TRACKER).send(new HitBuilders.ScreenViewBuilder().build());//for screen info
        }catch (Exception e){
            //activity might be null
            new TimberOnErrorAction1(e.getMessage());
        }
    }

    public static void sendGAActionEvent(String category, String action){
        HashMap<String, String> events = new HashMap<>();
        events.put(GAnalyticsProvider.CATEGORY, category);
        events.put(GAnalyticsProvider.ACTION, action);
        GAnalyticsProvider.sendGAEvents(events);
    }
}
