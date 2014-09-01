package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.PurchasingListener;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface AmazonInventoryFetcher<
        AmazonSKUType extends AmazonSKU,
        AmazonProductDetailsType extends AmazonProductDetail<AmazonSKUType>,
        AmazonExceptionType extends AmazonException>
    extends BillingInventoryFetcher<
        AmazonSKUType,
        AmazonProductDetailsType,
        AmazonExceptionType>,
        AmazonActor, PurchasingListener
{
}
