package com.tradehero.th.utils;

import com.tradehero.th.utils.metrics.tapstream.TapStreamType;

public class Constants
{
    // build constants
    public static final boolean RELEASE = true;

    public static final boolean USE_BETA_HOME_PAGE = true;

    public static final boolean DOGFOOD_BUILD = false;

    public static final boolean PICASSO_DEBUG = !RELEASE;

    private static final int COMMON_ITEM_PER_PAGE = RELEASE ? 42 : 10;

    public static final int TIMELINE_ITEM_PER_PAGE = COMMON_ITEM_PER_PAGE;

    public static final int LEADERBOARD_MARK_USER_ITEM_PER_PAGE = COMMON_ITEM_PER_PAGE;

    // this constant is dedicated for static content page (html, image, cdn that
    // may be needed later, for Api endpoint, refer to retrofit module, we want to make it
    // generic and easy to switch between endpoint (prod, dev, test server) as much as possible.

    public static final String BASE_STATIC_CONTENT_URL = "https://www.tradehero.mobi/";
    public static final String PRIVACY_TERMS_OF_SERVICE = "http://cn.tradehero.mobi/privacy";

    public static final String PRIVACY_TERMS_OF_USE = BASE_STATIC_CONTENT_URL + "terms";
    public static final String APP_HOME = BASE_STATIC_CONTENT_URL + "AppHome";

    // Request Header
    public static final String ACCEPT_ENCODING_GZIP = "gzip";
    public static final String TH_CLIENT_VERSION = "TH-Client-Version";
    public static final String AUTHORIZATION = "Authorization";
    public static final String TH_LANGUAGE_CODE = "TH-Language-Code";
    public static final String TH_CLIENT_TYPE = "TH-Client-Type";

    // Response Header
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_ENCODING_GZIP = ACCEPT_ENCODING_GZIP;
    public static final String TH_ERROR_CODE = "TH-Error-Code";

    // Google PlayStore
    public static final String PLAYSTORE_APP_ID = "com.tradehero.th";
    public static final String WECHAT_SHARE_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.tradehero.th&g_f=991653";

    // Localytics
    public static final String LOCALYTICS_APP_KEY_RELEASE = "10a8a8e1d386d096bfe1641-8b2cc16e-5b52-11e4-a386-005cf8cbabd8";
    public static final String LOCALYTICS_APP_KEY_DEBUG =   "8f24cffb81cc32757a011bd-a6396038-58f6-11e4-a5f7-009c5fda0a25";

    private static final int VERSION = 101;

    public static final TapStreamType TAP_STREAM_TYPE = TapStreamType.fromType(VERSION);

    public static final int SHARE_WEIBO_CONTENT_LENGTH_LIMIT = 140;
}
