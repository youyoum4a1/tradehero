package com.androidth.general.network;

public class NetworkConstants
{
    private static boolean isDebug = false; //just change this

    public static final String NO_COMPETITION = "https://live.tradehero.mobi/pages/nocompetition";

    private static final String TRADEHERO_PROD_ENDPOINT = "https://www.tradehero.mobi/";
    private static final String TRADEHERO_DEV_ENDPOINT = "https://dev.tradehero.mobi/";
    private static final String TRADEHERO_PROD_LIVE_ENDPOINT = "https://live.tradehero.mobi/";
    private static final String TRADEHERO_DEV_LIVE_ENDPOINT = "https://devlive.tradehero.mobi/";

    //used by Retrofit and Constants
    public static String BASE_URL = isDebug? TRADEHERO_DEV_ENDPOINT : TRADEHERO_PROD_ENDPOINT;

    public static String BASE_URL_LIVE = isDebug? TRADEHERO_DEV_LIVE_ENDPOINT : TRADEHERO_PROD_LIVE_ENDPOINT;

//    private static final String TRADEHERO_PROD_ENDPOINT = "http://192.168.1.10:1857/";//VJ local
//    private static final String TRADEHERO_DEV_ENDPOINT = "https://dev.tradehero.mobi/";//"thvm-hadoop.cloudapp.net";//
//    public static final String TRADEHERO_ENDPOINT = isDebug? TRADEHERO_DEV_ENDPOINT:TRADEHERO_PROD_ENDPOINT;

    //http://192.168.1.10:1857/
    //https://www.tradehero.mobi/

//    public static final String TRADEHERO_PROD_API_ENDPOINT = TRADEHERO_ENDPOINT + "api/"; //PRODUCTION
//    public static final String TRADEHERO_PROD_API_ENDPOINT = TRADEHERO_DEV_ENDPOINT + "api/"; //DEBUG

//    public static final String TRADEHERO_QA_ENDPOINT = "https://th-paas-test-dev1.cloudapp.net/";
//    public static final String TRADEHERO_QA_API_ENDPOINT = TRADEHERO_QA_ENDPOINT + "api/";

//    public static String getApiEndPointInUse()
//    {
//        return Constants.DOGFOOD_BUILD ? TRADEHERO_QA_API_ENDPOINT : TRADEHERO_PROD_API_ENDPOINT;
//    }


    public static String getApiEndPointInUse()
    {
        return BASE_URL;
    }

    public static final String COMPETITION_PATH = "pages/";

    /** Bing Translation */
    public static final String BING_TRANSLATION_ENDPOINT = "http://api.microsofttranslator.com";




    // Request Header
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ACCEPT_ENCODING_GZIP = "gzip";
    public static final String TH_CLIENT_VERSION = "TH-Client-Version";
    public static final String AUTHORIZATION = "Authorization";
    public static final String TH_LANGUAGE_CODE = "TH-Language-Code";
    public static final String TH_CLIENT_TYPE = "TH-Client-Type";
    public static final String USER_ID = "UserId";

    // Response Header
//    public static final String CONTENT_ENCODING = "Content-Encoding";
//    public static final String CONTENT_ENCODING_GZIP = ACCEPT_ENCODING_GZIP;
    public static final String TH_ERROR_CODE = "TH-Error-Code";
}
