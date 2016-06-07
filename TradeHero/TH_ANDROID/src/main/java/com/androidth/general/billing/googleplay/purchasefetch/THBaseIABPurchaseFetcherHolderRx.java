package com.androidth.general.billing.googleplay.purchasefetch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;
import com.androidth.general.common.billing.googleplay.purchasefetch.BaseIABPurchaseFetcherHolderRx;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;
import javax.inject.Inject;

public class THBaseIABPurchaseFetcherHolderRx
        extends BaseIABPurchaseFetcherHolderRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
        implements THIABPurchaseFetcherHolderRx
{
    @NonNull protected final Context context;
    @NonNull protected final IABExceptionFactory iabExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseFetcherHolderRx(
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super();
        this.context = context;
        this.iabExceptionFactory = iabExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected THBaseIABPurchaseFetcherRx createFetcher(int requestCode)
    {
        return new THBaseIABPurchaseFetcherRx(requestCode, context, iabExceptionFactory);
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
