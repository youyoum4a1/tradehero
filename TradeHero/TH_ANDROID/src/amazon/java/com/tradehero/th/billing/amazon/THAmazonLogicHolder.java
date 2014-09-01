package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonLogicHolder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THBillingLogicHolder;
import com.tradehero.th.billing.amazon.request.THAmazonRequestFull;

public interface THAmazonLogicHolder
    extends AmazonLogicHolder<
            AmazonSKUListKey,
            AmazonSKU,
            AmazonSKUList,
            THAmazonProductDetail,
            THAmazonPurchaseOrder,
            THAmazonOrderId,
            THAmazonPurchase,
            THAmazonRequestFull,
            AmazonException>,
        THBillingLogicHolder<
                AmazonSKUListKey,
                AmazonSKU,
                AmazonSKUList,
                THAmazonProductDetail,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase,
                THAmazonRequestFull,
                AmazonException>,
        THAmazonProductDetailDomainInformer,
        THAmazonProductIdentifierFetcherHolder,
        THAmazonInventoryFetcherHolder,
        THAmazonPurchaseFetcherHolder,
        THAmazonPurchaserHolder,
        THAmazonPurchaseReporterHolder,
        THAmazonPurchaseConsumerHolder
{
    void unregisterPurchaseConsumptionListener(int requestCode);
}
