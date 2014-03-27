package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungPurchaseCache;
import com.tradehero.common.billing.samsung.SamsungSKU;

/**
 * Created by xavier on 3/27/14.
 */
public class THSamsungPurchaseCache
    extends SamsungPurchaseCache<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase>
{
    public static final int MAX_SIZE = 300;

    public THSamsungPurchaseCache()
    {
        super(MAX_SIZE);
    }

}
