package com.tradehero.th.billing.googleplay.consumer;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BillingServiceBinderObservable;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.consume.BaseIABPurchaseConsumerRx;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;

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
            @NonNull IABExceptionFactory iabExceptionFactory,
            @NonNull BillingServiceBinderObservable billingServiceBinderObservable)
    {
        super(requestCode, purchase, context, iabExceptionFactory, billingServiceBinderObservable);
    }
    //</editor-fold>
}
