package com.tradehero.th.billing.samsung.persistence;

import com.tradehero.common.billing.samsung.persistence.SamsungPurchaseCache;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class THSamsungPurchaseCache
    extends SamsungPurchaseCache<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase>
{
    public static final int MAX_SIZE = 300;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungPurchaseCache()
    {
        super(MAX_SIZE);
    }
    //</editor-fold>
}
