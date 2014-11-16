package com.tradehero.common.billing.amazon.exception;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;

public class AmazonPurchaseUpdatesException extends AmazonException
{
    @NonNull public final PurchaseUpdatesResponse purchaseUpdatesResponse;

    //<editor-fold desc="Constructors">
    public AmazonPurchaseUpdatesException(String message,
            @NonNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        super(message);
        this.purchaseUpdatesResponse = purchaseUpdatesResponse;
    }
    //</editor-fold>
}
