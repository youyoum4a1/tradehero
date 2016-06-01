package com.ayondo.academy.billing.googleplay.consumer;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.consume.BaseIABPurchaseConsumerRx;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABPurchase;

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
