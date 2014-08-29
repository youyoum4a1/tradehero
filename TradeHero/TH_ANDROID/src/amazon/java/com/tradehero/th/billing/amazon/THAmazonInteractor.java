package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonInteractor;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.amazon.request.THAmazonRequestFull;
import com.tradehero.th.billing.amazon.request.THUIAmazonRequest;

public interface THAmazonInteractor
        extends
        AmazonInteractor<
                AmazonSKUListKey,
                AmazonSKU,
                AmazonSKUList,
                THAmazonProductDetail,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase,
                THAmazonLogicHolder,
                THAmazonRequestFull,
                THUIAmazonRequest,
                AmazonException>,
        THBillingInteractor<
                AmazonSKUListKey,
                AmazonSKU,
                AmazonSKUList,
                THAmazonProductDetail,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase,
                THAmazonLogicHolder,
                THAmazonRequestFull,
                THUIAmazonRequest,
                AmazonException>
{
}
