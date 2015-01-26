package com.tradehero.common.billing.amazon.exception;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.PurchaseResponse;

public class AmazonPurchaseReceiptCancelledException extends AmazonPurchaseException
{
    //<editor-fold desc="Constructors">
    public AmazonPurchaseReceiptCancelledException(
            String message,
            @NonNull String sku,
            @NonNull PurchaseResponse purchaseResponse)
    {
        super(message, sku, purchaseResponse);
    }
    //</editor-fold>
}
