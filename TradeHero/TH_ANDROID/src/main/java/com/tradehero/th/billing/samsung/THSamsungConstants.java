package com.tradehero.th.billing.samsung;

import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.tradehero.common.billing.samsung.SamsungConstants;
import com.tradehero.th.utils.Constants;


public class THSamsungConstants extends SamsungConstants
{
    public static final int PURCHASE_MODE = Constants.RELEASE ? SamsungIapHelper.IAP_MODE_COMMERCIAL : SamsungIapHelper.IAP_MODE_TEST_SUCCESS;

    public static final String IAP_ITEM_GROUP_ID = "100000103210d";

    // Below are the codes as they are understood on Samsung Store
    public static final String EXTRA_CASH_T0_ITEM_ID = "000001016731";
    public static final String EXTRA_CASH_T0_DATA_1 = "com.myhero.th.extracash.t0";

    public static final String EXTRA_CASH_T1_ITEM_ID = "000001016742";
    public static final String EXTRA_CASH_T1_DATA_1 = "com.myhero.th.extracash.t1";

    public static final String EXTRA_CASH_T2_ITEM_ID = "000001016743";
    public static final String EXTRA_CASH_T2_DATA_1 = "com.myhero.th.extracash.t2";
}
