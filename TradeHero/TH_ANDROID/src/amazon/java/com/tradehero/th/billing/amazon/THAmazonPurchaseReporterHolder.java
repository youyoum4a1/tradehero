package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THPurchaseReporterHolder;

public interface THAmazonPurchaseReporterHolder
    extends THPurchaseReporterHolder<
            AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase,
            AmazonException>
{
}
