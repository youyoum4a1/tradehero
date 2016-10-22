package com.androidth.general.billing;

import com.androidth.general.common.billing.BillingConstants;

public interface THBillingConstants extends BillingConstants
{
    int UNHANDLED_DOMAIN = -2000;
    int PURCHASE_REPORT_RETROFIT_ERROR = -2001;
    int MISSING_CACHED_DETAIL = -2002;
    int MISSING_APPLICABLE_PORTFOLIO_ID = -2003;
    int INVALID_QUANTITY = -2004;

    // Below are the codes as they may be returned from the server
    String SERVER_ALERT_1 = "TH.StockAlert.Subscription.0";
    String SERVER_ALERT_5 = "TH.StockAlert.Subscription.1";
    String SERVER_ALERT_UNLIMITED = "TH.StockAlert.Subscription.2";
}
