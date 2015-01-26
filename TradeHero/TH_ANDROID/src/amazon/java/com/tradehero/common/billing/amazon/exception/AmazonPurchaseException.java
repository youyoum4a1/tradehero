package com.tradehero.common.billing.amazon.exception;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.PurchaseResponse;

public class AmazonPurchaseException extends AmazonException
{
    @NonNull public final String sku;
    @NonNull public final PurchaseResponse purchaseResponse;

    public AmazonPurchaseException(String message,
            @NonNull String sku,
            @NonNull PurchaseResponse purchaseResponse)
    {
        super(message);
        this.sku = sku;
        this.purchaseResponse = purchaseResponse;
    }
}
