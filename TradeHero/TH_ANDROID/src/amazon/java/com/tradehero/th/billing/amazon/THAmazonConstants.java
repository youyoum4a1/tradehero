package com.ayondo.academy.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonConstants;
import com.ayondo.academy.billing.THBillingConstants;

public class THAmazonConstants
        extends AmazonConstants
        implements THBillingConstants
{
    // Below are the codes as they are understood on Google Play
    public static final String EXTRA_CASH_T0_KEY = "com.myhero.th.extracash.t0";
    public static final String EXTRA_CASH_T1_KEY = "com.myhero.th.extracash.t1";
    public static final String EXTRA_CASH_T2_KEY = "com.myhero.th.extracash.t2";

    public static final String RESET_PORTFOLIO_0 = "com.myhero.th.resetportfolio.0";

    // Below are the codes as they may be returned from the server
    public static final String SERVER_ALERT_1 = "TH.StockAlert.Subscription.0";
    public static final String SERVER_ALERT_5 = "TH.StockAlert.Subscription.1";
    public static final String SERVER_ALERT_UNLIMITED = "TH.StockAlert.Subscription.2";
}
