package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.BillingPurchaserHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface AmazonPurchaserHolder<
        AmazonSKUType extends AmazonSKU,
        AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonExceptionType extends AmazonException>
    extends BillingPurchaserHolder<
        AmazonSKUType,
        AmazonPurchaseOrderType,
        AmazonOrderIdType,
        AmazonPurchaseType,
        AmazonExceptionType>
{
}
