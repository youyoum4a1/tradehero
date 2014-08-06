package com.tradehero.th.billing.samsung.persistence;

import com.tradehero.common.billing.samsung.persistence.SamsungPurchaseCache;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import javax.inject.Inject;

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

    @Inject public THSamsungPurchaseCache()
    {
        super(MAX_SIZE);
    }
}
