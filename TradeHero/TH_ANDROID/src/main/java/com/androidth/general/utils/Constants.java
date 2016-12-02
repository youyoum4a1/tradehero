package com.androidth.general.utils;

import com.androidth.general.BuildConfig;
import com.androidth.general.api.misc.DeviceType;
import com.androidth.general.network.NetworkConstants;
import com.androidth.general.utils.metrics.tapstream.TapStreamType;

public class Constants
{
    public static final boolean RELEASE = !BuildConfig.DEBUG;

    public static final boolean ONBOARD_OANDA_ENABLED = false;

    public static final boolean USE_BETA_HOME_PAGE = true;

    public static final boolean DOGFOOD_BUILD = false;

    public static final boolean PICASSO_DEBUG = DOGFOOD_BUILD;

    public static final int COMMON_ITEM_PER_PAGE = RELEASE ? 42 : 20;

    public static final int TIMELINE_ITEM_PER_PAGE = COMMON_ITEM_PER_PAGE;

    public static final int LEADERBOARD_MARK_USER_ITEM_PER_PAGE = COMMON_ITEM_PER_PAGE;
    public static final int MAX_OWN_LEADER_RANKING = 1000;

    // this constant is dedicated for static content page (html, image, cdn that
    // may be needed later, for Api endpoint, refer to retrofit module, we want to make it
    // generic and easy to switch between endpoint (prod, dev, test server) as much as possible.

//    public static final String BASE_STATIC_CONTENT_URL = "https://www.tradehero.mobi/";

//        public static final String BASE_STATIC_CONTENT_URL = "https://dev.tradehero.mobi/";//DEV
//    public static final String BASE_STATIC_CONTENT_URL = "http://192.168.1.10:1857/";//VJ local

    public static final String PRIVACY_TERMS_OF_SERVICE = NetworkConstants.BASE_URL + "privacy";
    public static final String PRIVACY_TERMS_OF_USE = NetworkConstants.BASE_URL + "terms";
    public static final String APP_HOME = NetworkConstants.BASE_URL + "AppHome";

    public static final String WECHAT_SHARE_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.tradehero.th&g_f=991653";

    // Localytics
    public static final String LOCALYTICS_APP_KEY_RELEASE = "731adfbe0df8a59ff8e1117-4a8d02de-01d4-11e4-9d24-005cf8cbabd8";
    public static final String LOCALYTICS_APP_KEY_DEBUG = "eeace06a24de5f8f52697c5-c32afe1e-1eb1-11e4-234f-004a77f8b47f";

    // GCM
    public static final String GCM_STAGING_SENDER = "927417497470";
    public static final String TENCENT_APP_ID = "1101331512";

    // To change TAPSTREAM_VERSION, look at gradle build flavor for china
    public static final TapStreamType TAP_STREAM_TYPE = TapStreamType.fromType(BuildConfig.TAPSTREAM_VERSION);
    public static final DeviceType DEVICE_TYPE = Constants.TAP_STREAM_TYPE.marketSegment.deviceType;

    public static class Auth
    {
        public static final String PARAM_AUTHTOKEN_TYPE = "authTokenType";
        public static final String PARAM_ACCOUNT_TYPE = (RELEASE ? "" : "dev.") + "tradehero.mobi";
    }

    private Constants()
    {
    }

    public static final String REALM_DB_NAME = "TH_DB_REALM.realm";

    public static final String EXCEPTION_MESSAGE = "ExceptionMessage";
    public static final String EXCEPTION_TYPE = "ExceptionType";
    public static final String EXCEPTION_TYPE_FB = "Facebook.FacebookOAuthException";

    public static final String DATE_FORMAT_STANDARD = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_STANDARD_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT_PRECISE = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String DATE_FORMAT_PRECISE_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String TAPSTREAM_KEY = "Om-yveoZQ7CMU7nUGKlahw";
    public static final String TAPSTREAM_APP_NAME = "tradehero";


}
