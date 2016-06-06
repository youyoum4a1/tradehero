package com.androidth.general.billing;

import com.androidth.general.common.billing.BillingConstants;

public interface THBillingConstants extends BillingConstants
{
    final int UNHANDLED_DOMAIN = -2000;
    final int PURCHASE_REPORT_RETROFIT_ERROR = -2001;
    final int MISSING_CACHED_DETAIL = -2002;
    final int MISSING_APPLICABLE_PORTFOLIO_ID = -2003;
    final int INVALID_QUANTITY = -2004;

    // Below are the codes as they may be returned from the server
    final String SERVER_ALERT_1 = "TH.StockAlert.Subscription.0";
    final String SERVER_ALERT_5 = "TH.StockAlert.Subscription.1";
    final String SERVER_ALERT_UNLIMITED = "TH.StockAlert.Subscription.2";
}
