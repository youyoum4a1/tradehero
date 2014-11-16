package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface AmazonPurchaseConsumer<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonExceptionType extends AmazonException>
        extends AmazonActor
{
    OnAmazonConsumptionFinishedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> getConsumptionFinishedListener();
    void setConsumptionFinishedListener(
            OnAmazonConsumptionFinishedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> consumptionListener);
    void consume(AmazonPurchaseType purchase);

    public static interface OnAmazonConsumptionFinishedListener<
            AmazonSKUType extends AmazonSKU,
            AmazonOrderIdType extends AmazonOrderId,
            AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
            AmazonExceptionType extends AmazonException>
    {
        void onPurchaseConsumed(int requestCode, AmazonPurchaseType purchase);
        void onPurchaseConsumeFailed(int requestCode, AmazonPurchaseType purchase, AmazonExceptionType exception);
    }
}
