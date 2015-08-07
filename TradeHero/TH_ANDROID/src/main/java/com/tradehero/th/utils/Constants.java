package com.tradehero.th.utils;

import com.tradehero.th.BuildConfig;
import com.tradehero.th.utils.metrics.tapstream.TapStreamType;

public class Constants {
    // build constants
    public static final boolean RELEASE = !BuildConfig.DEBUG;

    public static final boolean DOGFOOD_BUILD = false;

    // this constant is dedicated for static content page (html, image, cdn that
    // may be needed later, for Api endpoint, refer to retrofit module, we want to make it
    // generic and easy to switch between endpoint (prod, dev, test server) as much as possible.

    public static final String PRIVACY_TERMS_OF_SERVICE = "http://cn.tradehero.mobi/privacy";

    // Request Header
    public static final String TH_CLIENT_VERSION = "TH-Client-Version";
    public static final String AUTHORIZATION = "Authorization";
    public static final String TH_LANGUAGE_CODE = "TH-Language-Code";
    public static final String TH_CLIENT_TYPE = "TH-Client-Type";

    // Response Header
    public static final String TH_ERROR_CODE = "TH-Error-Code";

    public static final String WECHAT_SHARE_URL_INSTALL_APP = "http://a.app.qq.com/o/simple.jsp?pkgname=com.tradehero.th&g_f=991653";

    // Localytics
    public static final String LOCALYTICS_APP_KEY_RELEASE = "10a8a8e1d386d096bfe1641-8b2cc16e-5b52-11e4-a386-005cf8cbabd8";
    public static final String LOCALYTICS_APP_KEY_DEBUG =   "8f24cffb81cc32757a011bd-a6396038-58f6-11e4-a5f7-009c5fda0a25";

        private static final int VERSION = 101;
    public static final TapStreamType TAP_STREAM_TYPE = TapStreamType.fromType(VERSION);

    public static final int SHARE_WEIBO_CONTENT_LENGTH_LIMIT = 140;

    public final static String EMAIL_FEEDBACK = "support@tradehero.mobi";

    public final static String DEFAULT_SHARE_ENDPOINT = "cn.tradehero.mobi";

    //Notification Id
    public final static int NOTIFICATION_ID = 90001;

    //Manager Account
    public final static boolean isManager = false;

    //HAITONG Environment
    public final static boolean isInHAITONGTestingEnvironment = true;
}
