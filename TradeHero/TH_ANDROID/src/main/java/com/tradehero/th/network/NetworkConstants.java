package com.tradehero.th.network;

import com.tradehero.th.utils.Constants;

public class NetworkConstants
{
    public static final String YAHOO_FINANCE_ENDPOINT = "http://finance.yahoo.com";
    //public static final String TRADEHERO_PROD_ENDPOINT = "https://www.tradehero.mobi/";
    //public static final String TRADEHERO_PROD_ENDPOINT = "https://cn.api.tradehero.mobi/";
    public static final String TRADEHERO_PROD_ENDPOINT = "https://cn1.api.tradehero.mobi/";

    public static final String TRADEHERO_PROD_API_ENDPOINT = TRADEHERO_PROD_ENDPOINT + "api/";
    //public static final String TRADEHERO_PROD_API_ENDPOINT = "http://jackbao.cloudapp.net/api/";
    //public static final String TRADEHERO_PROD_API_ENDPOINT = "http://jackfortune.chinacloudapp.cn/api/";

    //public static final String TRADEHERO_PROD_API_ENDPOINT = "http://192.168.20.8/api/";
    //public static final String TRADEHERO_PROD_API_ENDPOINT = "https://thvm-proxy.cloudapp.net:8000/api";

    //public static final String TRADEHERO_QA_ENDPOINT = "https://th-paas-test-dev1.cloudapp.net/"
    public static final String TRADEHERO_QA_ENDPOINT = "http://thapi-web-prod.chinacloudapp.cn/";
    public static final String TRADEHERO_QA_API_ENDPOINT = TRADEHERO_QA_ENDPOINT + "api/";

    public static final String HENGSHENG_ENDPOINT = "https://sandbox.hs.net";
    public static final String DRIVEWEALTH_ENDPOINT = "https://api.drivewealth.net";

    public static String getApiEndPointInUse()
    {
        return Constants.DOGFOOD_BUILD ? TRADEHERO_QA_API_ENDPOINT : TRADEHERO_PROD_API_ENDPOINT;
    }

    public static final String COMPETITION_PATH = "competitionpages/";

    /** Bing Translation */
    public static final String BING_TRANSLATION_ENDPOINT = "http://api.microsofttranslator.com";
}
