package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.PurchaseResponse;
import org.jetbrains.annotations.NotNull;

abstract public class BaseAmazonPurchase<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId>
        implements AmazonPurchase<
        AmazonSKUType,
        AmazonOrderIdType>
{
    @NotNull protected final PurchaseResponse purchaseResponse;

    //<editor-fold desc="Constructors">
    protected BaseAmazonPurchase(@NotNull PurchaseResponse purchaseResponse)
    {
        this.purchaseResponse = purchaseResponse;
    }
    //</editor-fold>
}
