package com.tradehero.th.billing.amazon;

import com.amazon.device.iap.model.Receipt;
import com.tradehero.common.billing.amazon.AmazonPurchaseIncomplete;
import com.tradehero.common.billing.amazon.AmazonSKU;
import org.jetbrains.annotations.NotNull;

public class THAmazonPurchaseIncomplete
        extends AmazonPurchaseIncomplete<
                        AmazonSKU,
                        THAmazonOrderId>
{
    //<editor-fold desc="Constructors">
    public THAmazonPurchaseIncomplete(@NotNull Receipt toCopyFrom)
    {
        super(toCopyFrom);
    }
    //</editor-fold>

    @Override @NotNull public AmazonSKU getProductIdentifier()
    {
        return new AmazonSKU(receipt.getSku());
    }

    @Override @NotNull public THAmazonOrderId getOrderId()
    {
        return new THAmazonOrderId(receipt);
    }
}
