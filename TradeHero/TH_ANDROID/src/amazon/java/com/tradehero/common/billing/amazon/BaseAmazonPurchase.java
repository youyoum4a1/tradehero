package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.PurchaseResponse;
import android.support.annotation.NonNull;

abstract public class BaseAmazonPurchase<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId>
        implements AmazonPurchase<
        AmazonSKUType,
        AmazonOrderIdType>
{
    @NonNull protected final PurchaseResponse purchaseResponse;

    //<editor-fold desc="Constructors">
    protected BaseAmazonPurchase(@NonNull PurchaseResponse purchaseResponse)
    {
        this.purchaseResponse = purchaseResponse;
    }
    //</editor-fold>

    @NonNull @Override public String getAmazonUserId()
    {
        return purchaseResponse.getUserData().getUserId();
    }
}
