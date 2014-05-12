package com.tradehero.th.billing.samsung;

import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.tradehero.common.billing.samsung.SamsungConstants;
import com.tradehero.common.billing.samsung.SamsungItemGroup;
import com.tradehero.th.billing.THBillingConstants;
import com.tradehero.th.utils.Constants;
import java.text.SimpleDateFormat;
import java.util.Date;

public class THSamsungConstants
        extends SamsungConstants
        implements THBillingConstants
{
    public static final int PURCHASE_MODE = Constants.RELEASE ? SamsungIapHelper.IAP_MODE_COMMERCIAL : SamsungIapHelper.IAP_MODE_TEST_SUCCESS;

    public static String INBOX_SIMPLE_DATE_FORMAT = "yyyyMMdd";
    public static String getTodayStringForInbox()
    {
        return new SimpleDateFormat(THSamsungConstants.INBOX_SIMPLE_DATE_FORMAT).format(new Date());
    }

    // All IAP items on the Samsung store are grouped under this GroupId.
    public static final String IAP_ITEM_GROUP_ID = "100000103210";
    public static SamsungItemGroup getItemGroupId()
    {
        return new SamsungItemGroup(IAP_ITEM_GROUP_ID);
    }

    // Below are the codes as they are understood on Samsung Store
    public static final String EXTRA_CASH_T0_DATA_1 = "com.myhero.th.extracash.t0";
    public static final String EXTRA_CASH_T1_DATA_1 = "com.myhero.th.extracash.t1";
    public static final String EXTRA_CASH_T2_DATA_1 = "com.myhero.th.extracash.t2";

    public static final String CREDIT_1_DATA_1 = "com.myhero.th.1cc";
    //public static final String CREDIT_5_DATA_1 = "com.myhero.th.5cc";
    public static final String CREDIT_10_DATA_1 = "com.myhero.th.10cc";
    public static final String CREDIT_20_DATA_1 = "com.myhero.th.20cc";

    public static final String RESET_PORTFOLIO_0_DATA_1 = "com.myhero.th.resetportfolio.0";

    public static final String ALERT_1_DATA_1 = "com.myhero.th.stockalert.subscription.t0";
    public static final String ALERT_5_DATA_1 = "com.myhero.th.stockalert.subscription.t1";
    public static final String ALERT_UNLIMITED_DATA_1 = "com.myhero.th.stockalert.subscription.t2";

    // Below are the codes as they may be returned from the server
    public static final String SERVER_ALERT_1 = "TH.StockAlert.Subscription.0";
    public static final String SERVER_ALERT_5 = "TH.StockAlert.Subscription.1";
    public static final String SERVER_ALERT_UNLIMITED = "TH.StockAlert.Subscription.2";
}
