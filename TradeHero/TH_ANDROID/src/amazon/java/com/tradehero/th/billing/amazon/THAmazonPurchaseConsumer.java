package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonPurchaseConsumer;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface THAmazonPurchaseConsumer
        extends AmazonPurchaseConsumer<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase,
        AmazonException>
{
}
