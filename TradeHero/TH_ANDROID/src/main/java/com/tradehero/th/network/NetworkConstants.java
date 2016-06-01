package com.ayondo.academy.network;

import com.ayondo.academy.utils.Constants;

public class NetworkConstants
{
    public static final String TRADEHERO_PROD_ENDPOINT = "https://www.tradehero.mobi/";//"thvm-hadoop.cloudapp.net";//
    public static final String TRADEHERO_PROD_API_ENDPOINT = TRADEHERO_PROD_ENDPOINT + "api/";
    public static final String TRADEHERO_QA_ENDPOINT = "https://th-paas-test-dev1.cloudapp.net/";
    public static final String TRADEHERO_QA_API_ENDPOINT = TRADEHERO_QA_ENDPOINT + "api/";

    public static String getApiEndPointInUse()
    {
        return Constants.DOGFOOD_BUILD ? TRADEHERO_QA_API_ENDPOINT : TRADEHERO_PROD_API_ENDPOINT;
    }

    public static final String COMPETITION_PATH = "competitionpages/";

    /** Bing Translation */
    public static final String BING_TRANSLATION_ENDPOINT = "http://api.microsofttranslator.com";
}
