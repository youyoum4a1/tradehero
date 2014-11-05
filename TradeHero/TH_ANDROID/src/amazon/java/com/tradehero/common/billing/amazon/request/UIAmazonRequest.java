package com.tradehero.common.billing.amazon.request;

import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonPurchaseConsumer;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import android.support.annotation.Nullable;

public interface UIAmazonRequest<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonExceptionType extends AmazonException>
{
    //<editor-fold desc="Consuming Purchase">
    boolean getConsumePurchase();
    void setConsumePurchase(boolean consumePurchase);
    boolean getPopIfConsumeFailed();
    @Nullable AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType,
            AmazonExceptionType> getConsumptionFinishedListener();
    void setConsumptionFinishedListener(
            @Nullable AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
                    AmazonSKUType,
                    AmazonOrderIdType,
                    AmazonPurchaseType,
                    AmazonExceptionType> consumptionFinishedListener);
    //</editor-fold>
}
