package com.androidth.general.common.billing.googleplay.consume;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseResult;
import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.billing.googleplay.IABPurchase;
import com.androidth.general.common.billing.googleplay.IABSKU;

public class PurchaseConsumeResult<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
    extends BaseResult
{
    @NonNull public final IABPurchaseType purchase;

    //<editor-fold desc="Constructors">
    public PurchaseConsumeResult(int requestCode, @NonNull IABPurchaseType purchase)
    {
        super(requestCode);
        this.purchase = purchase;
    }
    //</editor-fold>
}
