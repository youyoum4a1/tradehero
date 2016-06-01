package com.ayondo.academy.billing.googleplay.consumer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.consume.BaseIABPurchaseConsumerHolderRx;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABPurchase;
import javax.inject.Inject;

public class THBaseIABPurchaseConsumerHolderRx
        extends BaseIABPurchaseConsumerHolderRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
        implements THIABPurchaseConsumerHolderRx
{
    @NonNull protected final Context context;
    @NonNull protected final IABExceptionFactory iabExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseConsumerHolderRx(
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        this.context = context;
        this.iabExceptionFactory = iabExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected THBaseIABPurchaseConsumerRx createPurchaseConsumer(
            int requestCode,
            @NonNull THIABPurchase purchase)
    {
        return new THBaseIABPurchaseConsumerRx(requestCode, purchase, context, iabExceptionFactory);
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
