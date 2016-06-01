package com.ayondo.academy.billing.amazon.consume;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.consume.AmazonPurchaseConsumerRx;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;

public interface THAmazonPurchaseConsumerRx
        extends AmazonPurchaseConsumerRx<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase>
{
}
