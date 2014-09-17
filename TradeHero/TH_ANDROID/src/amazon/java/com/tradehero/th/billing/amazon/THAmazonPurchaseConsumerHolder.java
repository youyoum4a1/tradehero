package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonPurchaseConsumerHolder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface THAmazonPurchaseConsumerHolder
        extends AmazonPurchaseConsumerHolder<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase,
        AmazonException>
{
}
