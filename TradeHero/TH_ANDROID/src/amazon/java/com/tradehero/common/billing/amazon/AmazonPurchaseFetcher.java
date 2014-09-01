package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.PurchasingListener;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface AmazonPurchaseFetcher<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonExceptionType extends AmazonException>
        extends BillingPurchaseFetcher<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType,
        AmazonExceptionType>,
        AmazonActor, PurchasingListener
{
}
