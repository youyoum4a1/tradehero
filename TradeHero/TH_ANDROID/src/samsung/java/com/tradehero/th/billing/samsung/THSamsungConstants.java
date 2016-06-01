package com.ayondo.academy.billing.samsung;

import com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper;
import com.tradehero.common.billing.samsung.SamsungConstants;
import com.ayondo.academy.billing.THBillingConstants;
import com.ayondo.academy.utils.Constants;
import java.text.SimpleDateFormat;
import java.util.Date;

public class THSamsungConstants
        extends SamsungConstants
        implements THBillingConstants
{
    public static final int PURCHASE_MODE = Constants.RELEASE && !Constants.DOGFOOD_BUILD
            ? SamsungIapHelper.IAP_MODE_COMMERCIAL
            : SamsungIapHelper.IAP_MODE_TEST_SUCCESS;

    public static String INBOX_SIMPLE_DATE_FORMAT = "yyyyMMdd";
    public static String getTodayStringForInbox()
    {
        return new SimpleDateFormat(THSamsungConstants.INBOX_SIMPLE_DATE_FORMAT).format(new Date());
    }

    // Below are the codes as they are understood on Samsung Store
    public static final String EXTRA_CASH_T0_DATA_1 = "com.myhero.th.extracash.t0";
    public static final String EXTRA_CASH_T1_DATA_1 = "com.myhero.th.extracash.t1";
    public static final String EXTRA_CASH_T2_DATA_1 = "com.myhero.th.extracash.t2";

    public static final String RESET_PORTFOLIO_0_DATA_1 = "com.myhero.th.resetportfolio.0";

    // Below are the codes as they may be returned from the server
    public static final String SERVER_ALERT_1 = "TH.StockAlert.Subscription.0";
    public static final String SERVER_ALERT_5 = "TH.StockAlert.Subscription.1";
    public static final String SERVER_ALERT_UNLIMITED = "TH.StockAlert.Subscription.2";
}
