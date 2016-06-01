package com.ayondo.academy.billing.amazon.exception;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;

public class AmazonMissingCachedProductDetailForPurchaseException extends AmazonException
{
    @NonNull public final THAmazonPurchase purchase;

    //<editor-fold desc="Constructors">
    public AmazonMissingCachedProductDetailForPurchaseException(String message,
            @NonNull THAmazonPurchase purchase)
    {
        super(message);
        this.purchase = purchase;
    }
    //</editor-fold>
}
