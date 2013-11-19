package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 4:45 PM To change this template use File | Settings | File Templates. */
public class THIABPurchaseConsumer extends IABPurchaseConsumer<IABSKU, THIABOrderId, SKUPurchase>
{
    public static final String TAG = THIABPurchaseConsumer.class.getSimpleName();

    public THIABPurchaseConsumer(Activity activity)
    {
        super(activity);
    }
}
