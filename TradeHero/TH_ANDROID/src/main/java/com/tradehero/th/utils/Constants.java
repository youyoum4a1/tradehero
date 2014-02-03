package com.tradehero.th.utils;

import com.tradehero.th.BuildConfig;
import twitter4j.auth.RequestToken;

public class Constants
{
    // build constants
    public static final boolean RELEASE = !BuildConfig.DEBUG;

    public static final boolean PICASSO_DEBUG = RELEASE;

    private static final int COMMON_ITEM_PER_PAGE = 42;

    public static final int TIMELINE_ITEM_PER_PAGE = COMMON_ITEM_PER_PAGE;

    public static final int LEADERBOARD_MARK_USER_ITEM_PER_PAGE = COMMON_ITEM_PER_PAGE;


    //Linked In
    public static final String LINKEDIN_CONSUMER_KEY = "afed437khxve";
    public static final String LINKEDIN_CONSUMER_SECRET = "hO7VeSyL4y1W2ZiK";
    public static final String FACEBOOK_PROFILE_PICTURE = "http://graph.facebook.com/%s/picture?type=large";
    public static String OAUTH_CALLBACK_URL = "x-oauthflow-linkedin://callback";

    //Twitter
    public static RequestToken requestToken;
    public static String TWITTER_CONSUMER_KEY = "sJY7n9k29TAhraq4VjDYeg";
            // place your cosumer key here
    public static String TWITTER_CONSUMER_SECRET = "gRLhwCd3YgdaKKEH7Gwq9FI75TJuqHfi2TiDRwUHo";
            // place your consumer secret here
    //public static String TWITTER_CALLBACK_URL = "x-oauthflow-twitter://callback";
    // Preference Constants

    // Twitter oauth urls
    public static final String URL_TWITTER_AUTH = "https://api.twitter.com/oauth/request_token";
    public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static final String URL_TWITTER_OAUTH_TOKEN = "https://api.twitter.com/oauth/access_token";

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
    public static final String BASE_API_URL = BASE_TH_URL + "api/";
    public static final String SIGN_UP_WITH_EMAIL_URL = BASE_API_URL + "SignupWithEmail";
    public static final String SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL = BASE_API_URL + "users";
    public static final String LOGIN_URL = BASE_API_URL + "login";
    public static final String PRIVACY_WEB_URL = BASE_TH_URL + "privacy";
    public static final String PRIVACY_TERMS_OF_SERVICE = BASE_TH_URL + "privacy";
    public static final String FORGOT_PASSWORD = BASE_API_URL + "forgotPassword";
    public static final String CHECK_NAME_URL = BASE_API_URL + "checkDisplayNameAvailable?displayName=";

    //header
    public static final String CONTENT_TYPE = "Content-type";
    public static final String CONTENT_TYPE_VALUE_URL_ENCODED = "application/x-www-form-urlencoded; charset=utf-8";
    public static final String CONTENT_TYPE_VALUE_JSON = "application/json";
    public static final String CHARSET = "charset";
    public static final String CHARSET_VALUE = "utf-8";

    //Header
    public static final String TH_CLIENT_VERSION = "TH-Client-Version";
    public static final String TH_CLIENT_VERSION_VALUE = "1.5.9.3451";
    public static final String AUTHORIZATION = "Authorization";

    public static final String TH_FB_PREFIX = "TH-Facebook";
    public static final String TH_TWITTER_PREFIX = "TH-Twitter";
    public static final String TH_LINKEDIN_PREFIX = "TH-LinkedIn";
    public static final String TH_EMAIL_PREFIX = "Basic";
    public static final String TH_ENTITY = "{\"clientVersion\":\"1.5.1\",\"clientiOS\":1}";

    //LINKEDiN
    public static final String LINKED_ACCESS_TOKEN = "linkedin_access_token";
    public static final String LINKED_ACCESS_TOKEN_SCERET = "linkedin_access_token_secret";
}
