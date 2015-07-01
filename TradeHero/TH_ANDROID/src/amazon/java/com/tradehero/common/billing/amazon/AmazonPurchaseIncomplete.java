package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;

abstract public class AmazonPurchaseIncomplete<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId>
        implements AmazonPurchase<
        AmazonSKUType,
        AmazonOrderIdType>
{
    @NonNull protected final Receipt receipt;
    @NonNull protected final UserData userData;

    //<editor-fold desc="Constructors">
    protected AmazonPurchaseIncomplete(@NonNull Receipt receipt, @NonNull UserData userData)
    {
        this.receipt = receipt;
        this.userData = userData;
    }
    //</editor-fold>
}
