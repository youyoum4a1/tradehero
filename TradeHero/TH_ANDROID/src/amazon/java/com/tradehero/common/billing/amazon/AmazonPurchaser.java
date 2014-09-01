package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.PurchasingListener;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface AmazonPurchaser<
        AmazonSKUType extends AmazonSKU,
        AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonExceptionType extends AmazonException>
        extends BillingPurchaser<
        AmazonSKUType,
        AmazonPurchaseOrderType,
        AmazonOrderIdType,
        AmazonPurchaseType,
        AmazonExceptionType>,
        AmazonActor, PurchasingListener
{
}
