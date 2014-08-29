package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.persistence.billing.googleplay.IABPurchaseCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class THIABPurchaseCache
        extends IABPurchaseCache<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THIABPurchaseCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>
}
