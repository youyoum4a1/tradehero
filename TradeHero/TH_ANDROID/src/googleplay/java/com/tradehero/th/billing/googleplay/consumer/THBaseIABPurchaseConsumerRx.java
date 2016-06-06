package com.androidth.general.billing.googleplay.consumer;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.consume.BaseIABPurchaseConsumerRx;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;

public class THBaseIABPurchaseConsumerRx
        extends BaseIABPurchaseConsumerRx<
                IABSKU,
                THIABOrderId,
                THIABPurchase>
    implements THIABPurchaseConsumerRx
{
    //<editor-fold desc="Constructors">
    public THBaseIABPurchaseConsumerRx(
            int requestCode,
            THIABPurchase purchase,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, purchase, context, iabExceptionFactory);
    }
    //</editor-fold>
}
