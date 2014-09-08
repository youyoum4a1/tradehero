package com.tradehero.th.persistence.billing;

import com.tradehero.common.billing.amazon.AmazonPurchaseCache;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class THAmazonPurchaseCache
        extends AmazonPurchaseCache<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THAmazonPurchaseCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>
}
