package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.RequestCodeHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface AmazonPurchaseConsumerHolder<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonExceptionType extends AmazonException>
    extends RequestCodeHolder
{
    AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType,
            AmazonExceptionType> getConsumptionFinishedListener(int requestCode);
    void registerConsumptionFinishedListener(int requestCode, AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType,
            AmazonExceptionType> purchaseConsumeHandler);
    void launchConsumeSequence(int requestCode, AmazonPurchaseType purchase);
}
