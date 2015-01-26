package com.tradehero.common.billing.amazon.consume;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseResult;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;

public class PurchaseConsumedResult<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>> extends BaseResult
{
    @NonNull public final AmazonPurchaseType purchase;

    //<editor-fold desc="Constructors">
    public PurchaseConsumedResult(
            int requestCode,
            @NonNull AmazonPurchaseType purchase)
    {
        super(requestCode);
        this.purchase = purchase;
    }
    //</editor-fold>
}
