package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.BillingInventoryFetcherHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface AmazonInventoryFetcherHolder<
        AmazonSKUType extends AmazonSKU,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>,
        AmazonExceptionType extends AmazonException>
    extends BillingInventoryFetcherHolder<
        AmazonSKUType,
        AmazonProductDetailType,
        AmazonExceptionType>
{
}
