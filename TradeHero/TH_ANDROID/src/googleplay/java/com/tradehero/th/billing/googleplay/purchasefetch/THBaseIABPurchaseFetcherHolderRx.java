package com.tradehero.th.billing.googleplay.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.purchasefetch.BaseIABPurchaseFetcherHolderRx;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;
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
}
