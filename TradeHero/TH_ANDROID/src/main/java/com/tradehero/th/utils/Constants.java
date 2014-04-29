package com.tradehero.th.utils;

public class Constants
{
    // build constants
    // TODO fix
    public static final boolean RELEASE = true; // !BuildConfig.DEBUG;

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

    // Google PlayStore
    public static final String PLAYSTORE_APP_ID = "com.tradehero.th";
    public static final String WECHAT_SHARE_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.tradehero.th&g_f=991653";

    //alipay
    public static final String ALIPAY_DEFAULT_PARTNER = "2088411057830429";
    public static final String ALIPAY_DEFAULT_SELLER = "chinaops@tradehero.mobi";
    public static final String ALIPAY_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALmiE1t/gfJZD4UcEQBjofT2G86i7J61lKiap6IhZH0bIo6vgGOiNE8ECmKr7MSLPeZrAc8D/6s/EqG8LRSB5kCjp2tXRcr/AEcXUsHo/T0xhiRRRD1ZfwLpT9dNEZrdcTK+zeGIeaA3p7rSrposB/Puwrfw77ybWhLwq3LrYU59AgMBAAECgYEAtnmRmbG/ZSES1n/+XmmR/KAoOXmCK4jG9u3/tqoog6o20BfIlygNT1bjdpcWCSXu1R9/CaYenpFDmWTG04/VaylwywT2/u1qBgd1WE0dXhWGt3rDSl2oF4uwu65U+qm4ox2G3L+6wP7SB6g4Ve96Gu8qUWPcQNIYQRyEGF2sOWkCQQD1CLvTtrS5pMamRUpESFXXZ7DvYIBoOXvDePZVtlUIPpDDKEtM9GP2IfGgKI57Ur+tGM1w9gnBJXbqdOyAA1w/AkEAwfDOaDwrj5eWd2nFjUVASj6SJkuj85MXeBR5oqP3KisWT7FXGjbMNFlvuvO61Qk/kZ6Yr8gWSImm4rTPEb1WQwJAIbae6SU1Pmmeb1gPPM/bB7UbkgPsBusJzT5CXk6k6mp5TnCn06G4cy/+Z7PMzsj22GEWOxmPBAUclGub8o0DzwJAG/uSzJdh8aX9n90zE98aF5xPmhbv4QUoLbtGkaD22K6+2WDNIIsni6Yb6O2h13suIOSxQyuri2vRGITeG8El4QJBAMfnNAv5gK6kHIG8bJSDxH7QTxzBy5NIoIadWsfiajnNAo5y+9X8T2jF9WD7D2vC9uJzmLaG46ioD0i0VYKMHuo=";
    public static final String ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    public static final int VERSION = 2;//0 for international, 1 baidu, 2 tencent
}
