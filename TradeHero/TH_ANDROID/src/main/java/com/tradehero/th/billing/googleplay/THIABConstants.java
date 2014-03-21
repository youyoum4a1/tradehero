package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABConstants;

/**
 * Created by xavier on 2/21/14.
 */
public class THIABConstants extends IABConstants
{
    public static final int UNHANDLED_DOMAIN = -2000;
    public static final int PURCHASE_REPORT_RETROFIT_ERROR = -2001;
    public static final int MISSING_CACHED_DETAIL = -2002;

    // Below are the codes as they are understood on Google Play
    public static final String EXTRA_CASH_T0_KEY = "com.myhero.th.extracash.t0";
    public static final String EXTRA_CASH_T1_KEY = "com.myhero.th.extracash.t1";
    public static final String EXTRA_CASH_T2_KEY = "com.myhero.th.extracash.t2";

    public static final String CREDIT_1 = "com.myhero.th.1cc";
    //public static final String CREDIT_5 = "com.myhero.th.5cc";
    public static final String CREDIT_10 = "com.myhero.th.10cc";
    public static final String CREDIT_20 = "com.myhero.th.20cc";

    public static final String RESET_PORTFOLIO_0 = "com.myhero.th.resetportfolio.0";
    public static final String ALERT_1 = "com.myhero.th.stockalert.subscription.t0";
    public static final String ALERT_5 = "com.myhero.th.stockalert.subscription.t1";
    public static final String ALERT_UNLIMITED = "com.myhero.th.stockalert.subscription.t2";

    // Below are the codes as they may be returned from the server
    public static final String SERVER_ALERT_1 = "TH.StockAlert.Subscription.0";
    public static final String SERVER_ALERT_5 = "TH.StockAlert.Subscription.1";
    public static final String SERVER_ALERT_UNLIMITED = "TH.StockAlert.Subscription.2";
}
