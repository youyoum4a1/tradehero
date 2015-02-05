package com.tradehero.common.billing.amazon.exception;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;

public class AmazonPurchaseUpdateFailedException extends AmazonPurchaseUpdatesException
{
    //<editor-fold desc="Constructors">
    public AmazonPurchaseUpdateFailedException(String message,
            @NonNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        super(message, purchaseUpdatesResponse);
    }
    //</editor-fold>
}
