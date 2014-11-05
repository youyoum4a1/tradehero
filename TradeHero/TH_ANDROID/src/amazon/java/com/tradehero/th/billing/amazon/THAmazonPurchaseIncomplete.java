package com.tradehero.th.billing.amazon;

import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;
import com.tradehero.common.billing.amazon.AmazonPurchaseIncomplete;
import com.tradehero.common.billing.amazon.AmazonSKU;
import android.support.annotation.NonNull;

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
}
