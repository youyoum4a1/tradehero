package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.BillingPurchaseFetcherHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface AmazonPurchaseFetcherHolder<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonExceptionType extends AmazonException>
    extends BillingPurchaseFetcherHolder<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType,
        AmazonExceptionType>
{
}
