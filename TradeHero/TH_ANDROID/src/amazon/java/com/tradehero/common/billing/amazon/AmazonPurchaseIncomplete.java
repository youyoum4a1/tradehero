package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;
import org.jetbrains.annotations.NotNull;

abstract public class AmazonPurchaseIncomplete<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId>
        implements AmazonPurchase<
        AmazonSKUType,
        AmazonOrderIdType>
{
    @NotNull protected final Receipt receipt;
    @NotNull protected final UserData userData;

    //<editor-fold desc="Constructors">
    protected AmazonPurchaseIncomplete(@NotNull Receipt receipt, @NotNull UserData userData)
    {
        this.receipt = receipt;
        this.userData = userData;
    }
    //</editor-fold>
}
