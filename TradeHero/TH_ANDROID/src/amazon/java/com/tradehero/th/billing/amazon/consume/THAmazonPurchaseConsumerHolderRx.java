package com.ayondo.academy.billing.amazon.consume;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.consume.AmazonPurchaseConsumerHolderRx;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;

public interface THAmazonPurchaseConsumerHolderRx
        extends AmazonPurchaseConsumerHolderRx<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase>
{
}
