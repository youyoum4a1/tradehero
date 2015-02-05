package com.tradehero.common.billing.amazon.purchasefetch;

import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.purchasefetch.BillingPurchaseFetcherHolderRx;

public interface AmazonPurchaseFetcherHolderRx<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends BillingPurchaseFetcherHolderRx<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType>
{
}
