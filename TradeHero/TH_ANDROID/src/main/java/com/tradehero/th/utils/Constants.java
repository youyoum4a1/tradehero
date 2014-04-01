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
    //TODO should not declare version hero
    public static final String TH_CLIENT_VERSION_VALUE = "1.6.2.3501";
    public static final String AUTHORIZATION = "Authorization";

    // Google PlayStore
    public static final String PLAYSTORE_APP_ID = "com.tradehero.th";
    public static final String WECHAT_SHARE_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.tradehero.th&g_f=991653";

    //alipay
    public static final String ALIPAY_DEFAULT_PARTNER = "2088101568358171";
    public static final String ALIPAY_DEFAULT_SELLER = "alipay-test09@alipay.com";
    public static final String ALIPAY_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMspRqs3boEbxaqz8ZR/VUAm0Aw+5klaQzTtYEMKHBQXZTePOdPhDiaThtQutfVrlDh2k8dukf+yCcVeIcOJRqukDW9D+rg7xVdNlWGHuUNaMaiZbPg3XsVDNG+MP8ZV/vyYeFFi8ypOkCt/8/GDxAaWbOg/oGdfoiAJJbjswZ/vAgMBAAECgYA7AFTGusV7923ToojBYK2IgP0g4U+N9AnaoCm5roDzEMxTc2QO9ahfaa7ZhmtPyBt2vnEylRkPkkwmJq1VlVORVo5NhGHqW0e1YtuCRWZGS6x/6EWdKn2nUgNSiUAiciWstMNfi+MWt2U7m7pkZdTvabucxZixeI7WM5Oxndzc+QJBAPjgV3JdafsSRYJLhwiXU+9oipTGY5oAsVYxwU4wTH9hahFCxLog3Q5xqfmgBPtb6bdzoW19rapc4pxBXCxC4i0CQQDQ+fQ04q+agaThFFU7ZyC2mCnpTJeLpHPUw6wD8i9p9lHQEYns8Fd3WMyy+C13zfSQmS5FbPGCleQtSXBa4ogLAkAYzDGqYYhnzeBDJUdlIb7pQd9dB49xDtScpASAx+s3Xft1kNONQC0GfWjUSI92hCf7cXgKMtWU/gBOVWzbtCZZAkBASKOWoSTjon3Vvyt42oB1qtk5qxXzHuOCz65aiGWNcvg3yS1kdYpybB6L70wNTo2s7XIOaTThtro6NB0b2BOBAkEAmF9/rdipc/7UGpHrbUJPKAZBGwbE1tRoHFbWWXWooBvHqh/z+9ElApUo7ttPUsdCdTGuPHIw8Cy1tFdpfDGiwQ==";
    public static final String ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
}
