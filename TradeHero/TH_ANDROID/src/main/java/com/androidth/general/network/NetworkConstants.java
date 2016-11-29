package com.androidth.general.network;

import com.androidth.general.utils.Constants;

public class NetworkConstants
{
    private static boolean isDebug = false; //just change this

    private static final String TRADEHERO_PROD_ENDPOINT = "https://www.tradehero.mobi/";//"thvm-hadoop.cloudapp.net";//

//    private static final String TRADEHERO_PROD_ENDPOINT = "http://192.168.1.10:1857/";//VJ local

    private static final String TRADEHERO_DEV_ENDPOINT = "https://dev.tradehero.mobi/";//"thvm-hadoop.cloudapp.net";//

    public static final String TRADEHERO_ENDPOINT = isDebug? TRADEHERO_DEV_ENDPOINT:TRADEHERO_PROD_ENDPOINT;

    //http://192.168.1.10:1857/
    //https://www.tradehero.mobi/

    public static final String TRADEHERO_PROD_API_ENDPOINT = TRADEHERO_ENDPOINT + "api/"; //PRODUCTION
//    public static final String TRADEHERO_PROD_API_ENDPOINT = TRADEHERO_DEV_ENDPOINT + "api/"; //DEBUG

    public static final String TRADEHERO_QA_ENDPOINT = "https://th-paas-test-dev1.cloudapp.net/";
    public static final String TRADEHERO_QA_API_ENDPOINT = TRADEHERO_QA_ENDPOINT + "api/";
    public static final String NO_COMPETITION = "https://live.tradehero.mobi/pages/nocompetition";
    public static String getApiEndPointInUse()
    {
        return Constants.DOGFOOD_BUILD ? TRADEHERO_QA_API_ENDPOINT : TRADEHERO_PROD_API_ENDPOINT;
    }

    public static final String COMPETITION_PATH = "pages/";

    /** Bing Translation */
    public static final String BING_TRANSLATION_ENDPOINT = "http://api.microsofttranslator.com";
}
