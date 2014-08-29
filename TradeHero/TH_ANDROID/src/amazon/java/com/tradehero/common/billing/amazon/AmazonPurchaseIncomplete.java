package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.Receipt;
import com.tradehero.common.billing.ProductPurchase;
import org.jetbrains.annotations.NotNull;

abstract public class AmazonPurchaseIncomplete<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId>
        implements AmazonPurchase<
        AmazonSKUType,
        AmazonOrderIdType>
{
    @NotNull protected final Receipt receipt;

    //<editor-fold desc="Constructors">
    protected AmazonPurchaseIncomplete(@NotNull Receipt receipt)
    {
        this.receipt = receipt;
    }
    //</editor-fold>
}
