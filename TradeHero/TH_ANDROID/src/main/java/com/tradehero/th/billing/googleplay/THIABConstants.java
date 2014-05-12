package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.th.billing.THBillingConstants;

public class THIABConstants
        extends IABConstants
        implements THBillingConstants
{
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
