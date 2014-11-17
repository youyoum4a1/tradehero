package com.tradehero.th.billing.amazon.consume;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.consume.AmazonPurchaseConsumerHolderRx;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonPurchase;

public interface THAmazonPurchaseConsumerHolderRx
        extends AmazonPurchaseConsumerHolderRx<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase>
{
}
