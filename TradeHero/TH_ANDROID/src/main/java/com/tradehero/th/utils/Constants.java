package com.tradehero.th.utils;

import com.tradehero.th.BuildConfig;
import twitter4j.auth.RequestToken;

public class Constants
{
    // build constants
    // TODO fix
    public static final boolean RELEASE = false; // !BuildConfig.DEBUG;

    public static final boolean PICASSO_DEBUG = !RELEASE;

    private static final int COMMON_ITEM_PER_PAGE = 42;

    public static final int TIMELINE_ITEM_PER_PAGE = COMMON_ITEM_PER_PAGE;

    public static final int LEADERBOARD_MARK_USER_ITEM_PER_PAGE = COMMON_ITEM_PER_PAGE;

    public static final String FACEBOOK_PROFILE_PICTURE = "http://graph.facebook.com/%s/picture?type=large";

    // TestFlightApp
    public static final boolean TEST_FLIGHT_ENABLED = true;
    public static final String TEST_FLIGHT_TOKEN = "a8a266bb-500c-4cdf-a5b0-f9c5bd6ad995";
    public static final boolean TEST_FLIGHT_REPORT_CHECKPOINT = true;
    public static final boolean TEST_FLIGHT_REPORT_LOG = true;

    //URL
    //public static final String BASE_TH_URL = "http://192.168.1.64:1857/";
    //public static final String BASE_TH_URL = "https://192.168.1.64:44301/";
    //public static final String BASE_TH_URL = "http://truongtho.noip.me/";
    public static final String BASE_TH_URL = "https://www.tradehero.mobi/";
    public static final String PRIVACY_TERMS_OF_SERVICE = BASE_TH_URL + "privacy";

    //Header
    public static final String TH_CLIENT_VERSION = "TH-Client-Version";
    public static final String TH_CLIENT_VERSION_VALUE = "1.5.9.3451";
    public static final String AUTHORIZATION = "Authorization";
}
