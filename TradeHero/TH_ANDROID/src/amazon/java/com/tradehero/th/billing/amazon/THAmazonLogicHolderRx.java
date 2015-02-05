package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonLogicHolderRx;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.th.billing.THBillingLogicHolderRx;

public interface THAmazonLogicHolderRx
        extends AmazonLogicHolderRx<
        AmazonSKUListKey,
        AmazonSKU,
        AmazonSKUList,
        THAmazonProductDetail,
        THAmazonPurchaseOrder,
        THAmazonOrderId,
        THAmazonPurchase>,
        THBillingLogicHolderRx<
                AmazonSKUListKey,
                AmazonSKU,
                AmazonSKUList,
                THAmazonProductDetail,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase>,
        THAmazonProductDetailDomainInformer
{
}
