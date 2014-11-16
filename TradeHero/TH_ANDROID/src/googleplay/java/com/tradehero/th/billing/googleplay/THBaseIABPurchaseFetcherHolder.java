package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABPurchaseFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.persistence.billing.googleplay.THIABPurchaseCacheRx;
import dagger.Lazy;
import javax.inject.Inject;

public class THBaseIABPurchaseFetcherHolder
        extends BaseIABPurchaseFetcherHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THIABPurchaseFetcher>
        implements THIABPurchaseFetcherHolder
{
    @NonNull protected final Context context;
    @NonNull protected final Lazy<IABExceptionFactory> iabExceptionFactory;
    @NonNull protected final THIABPurchaseCacheRx thiabPurchaseCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseFetcherHolder(
            @NonNull Context context,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory,
            @NonNull THIABPurchaseCacheRx thiabPurchaseCache)
    {
        super();
        this.context = context;
        this.iabExceptionFactory = iabExceptionFactory;
        this.thiabPurchaseCache = thiabPurchaseCache;
    }
    //</editor-fold>

    @Override protected THIABPurchaseFetcher createPurchaseFetcher(int requestCode)
    {
        return new THBaseIABPurchaseFetcher(requestCode, context, iabExceptionFactory, thiabPurchaseCache);
    }
}
