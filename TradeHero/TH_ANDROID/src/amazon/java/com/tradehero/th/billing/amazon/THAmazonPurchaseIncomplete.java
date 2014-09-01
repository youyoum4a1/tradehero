package com.tradehero.th.billing.amazon;

import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;
import com.tradehero.common.billing.amazon.AmazonPurchaseIncomplete;
import com.tradehero.common.billing.amazon.AmazonSKU;
import org.jetbrains.annotations.NotNull;

public class THAmazonPurchaseIncomplete
        extends AmazonPurchaseIncomplete<
                        AmazonSKU,
                        THAmazonOrderId>
{
    //<editor-fold desc="Constructors">
    public THAmazonPurchaseIncomplete(@NotNull Receipt toCopyFrom, @NotNull UserData userData)
    {
        super(toCopyFrom, userData);
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

    @NotNull @Override public String getAmazonUserId()
    {
        return userData.getUserId();
    }
}
