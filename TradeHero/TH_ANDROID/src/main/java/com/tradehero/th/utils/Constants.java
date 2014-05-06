package com.tradehero.th.utils;

public class Constants
{
    // build constants
    // TODO fix
    public static final boolean RELEASE = false; // !BuildConfig.DEBUG;

    public static final boolean PICASSO_DEBUG = !RELEASE;

    private static final int COMMON_ITEM_PER_PAGE = RELEASE ? 42 : 10;

    public static final int TIMELINE_ITEM_PER_PAGE = COMMON_ITEM_PER_PAGE;

    public static final int LEADERBOARD_MARK_USER_ITEM_PER_PAGE = COMMON_ITEM_PER_PAGE;

    public static final String FACEBOOK_PROFILE_PICTURE = "http://graph.facebook.com/%s/picture?type=large";

    // TestFlightApp
    public static final boolean TEST_FLIGHT_ENABLED = true;
    public static final String TEST_FLIGHT_TOKEN = "a8a266bb-500c-4cdf-a5b0-f9c5bd6ad995";
    public static final boolean TEST_FLIGHT_REPORT_CHECKPOINT = true;
    public static final boolean TEST_FLIGHT_REPORT_LOG = true;

    // this constant is dedicated for static content page (html, image, cdn that
    // may be needed later, for Api endpoint, refer to retrofit module, we want to make it
    // generic and easy to switch between endpoint (prod, dev, test server) as much as possible.
    public static final String BASE_STATIC_CONTENT_URL = "https://www.tradehero.mobi/";
    public static final String PRIVACY_TERMS_OF_SERVICE = BASE_STATIC_CONTENT_URL + "privacy";

    //Header
    public static final String TH_CLIENT_VERSION = "TH-Client-Version";
    public static final String AUTHORIZATION = "Authorization";
    public static final String TH_LANGUAGE_CODE = "TH-Language-Code";
    public static final String TH_CLIENT_TYPE = "TH-Client-Type";

    // Google PlayStore
    public static final String PLAYSTORE_APP_ID = "com.tradehero.th";
    public static final String WECHAT_SHARE_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.tradehero.th&g_f=991653";

    public static final int VERSION = 0;//0 for international, 1 baidu, 2 tencent
}
