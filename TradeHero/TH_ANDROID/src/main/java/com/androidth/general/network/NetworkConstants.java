package com.androidth.general.network;

import com.androidth.general.utils.Constants;

public class NetworkConstants
{
    public static final String TRADEHERO_PROD_ENDPOINT = "https://dev.tradehero.mobi/";//"thvm-hadoop.cloudapp.net";//
    //http://192.168.1.10:1857/
    //https://www.tradehero.mobi/
    public static final String TRADEHERO_PROD_API_ENDPOINT = TRADEHERO_PROD_ENDPOINT + "api/";
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
