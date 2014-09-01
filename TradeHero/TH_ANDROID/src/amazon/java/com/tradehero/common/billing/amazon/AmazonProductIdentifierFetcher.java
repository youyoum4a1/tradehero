package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.PurchasingListener;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface AmazonProductIdentifierFetcher<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>,
        AmazonExceptionType extends AmazonException>
    extends ProductIdentifierFetcher<
        AmazonSKUListKeyType,
        AmazonSKUType,
        AmazonSKUListType,
        AmazonExceptionType>,
        AmazonActor, PurchasingListener
{
}
