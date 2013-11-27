package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 4:45 PM To change this template use File | Settings | File Templates. */
public class THIABPurchaseConsumer extends IABPurchaseConsumer<IABSKU, THIABOrderId, BaseIABPurchase>
{
    public static final String TAG = THIABPurchaseConsumer.class.getSimpleName();

    public THIABPurchaseConsumer(Activity activity)
    {
        super(activity);
    }
}
