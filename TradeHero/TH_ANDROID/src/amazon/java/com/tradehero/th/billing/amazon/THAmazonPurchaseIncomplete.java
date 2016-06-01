package com.ayondo.academy.billing.amazon;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;
import com.tradehero.common.billing.amazon.AmazonPurchaseIncomplete;
import com.tradehero.common.billing.amazon.AmazonSKU;

public class THAmazonPurchaseIncomplete
        extends AmazonPurchaseIncomplete<
                        AmazonSKU,
                        THAmazonOrderId>
{
    //<editor-fold desc="Constructors">
    public THAmazonPurchaseIncomplete(@NonNull Receipt toCopyFrom, @NonNull UserData userData)
    {
        super(toCopyFrom, userData);
    }
    //</editor-fold>

    @Override @NonNull public AmazonSKU getProductIdentifier()
    {
        return new AmazonSKU(receipt.getSku());
    }

    @Override @NonNull public THAmazonOrderId getOrderId()
    {
        return new THAmazonOrderId(receipt);
    }

    @NonNull @Override public String getAmazonUserId()
    {
        return userData.getUserId();
    }

    @Override public boolean isCancelled()
    {
        return receipt.isCanceled();
    }
}
