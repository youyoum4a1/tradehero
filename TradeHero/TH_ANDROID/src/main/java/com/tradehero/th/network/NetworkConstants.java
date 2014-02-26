package com.tradehero.th.network;

import com.tradehero.th.utils.Constants;

/**
 * Created by xavier on 2/20/14.
 */
public class NetworkConstants
{
    public static final String YAHOO_FINANCE_ENDPOINT = "http://finance.yahoo.com";
    //public static final String TRADEHERO_DEV_ENDPOINT = "http://truongtho.noip.me/api/";
    private static final String TRADEHERO_DEV_ENDPOINT = "https://th-paas-test-dev1.cloudapp.net/api/";
    private static final String TRADEHERO_PROD_ENDPOINT = "https://www.tradehero.mobi/api/";
    public static final String COMPETITION_PATH = "competitionpages/";

    public static String getApiEndPoint()
    {
        return Constants.RELEASE ? TRADEHERO_PROD_ENDPOINT : TRADEHERO_DEV_ENDPOINT;
    }
}
