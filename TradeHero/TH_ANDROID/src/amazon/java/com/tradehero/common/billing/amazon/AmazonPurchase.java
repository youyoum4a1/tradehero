package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.PurchaseResponse;
import com.tradehero.common.billing.ProductPurchase;
import org.jetbrains.annotations.NotNull;

abstract public class AmazonPurchase<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId>
        implements ProductPurchase<
        AmazonSKUType,
        AmazonOrderIdType>
{
    @NotNull protected final PurchaseResponse purchaseResponse;

    //<editor-fold desc="Constructors">
    protected AmazonPurchase(@NotNull PurchaseResponse purchaseResponse)
    {
        this.purchaseResponse = purchaseResponse;
    }
    //</editor-fold>
}
