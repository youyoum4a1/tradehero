package com.tradehero.common.billing.amazon.exception;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;

public class AmazonPurchaseUpdateUnsupportedException extends AmazonPurchaseUpdatesException
{
    //<editor-fold desc="Constructors">
    public AmazonPurchaseUpdateUnsupportedException(String message, @NonNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        super(message, purchaseUpdatesResponse);
    }
    //</editor-fold>
}
