package com.tradehero.th.billing.samsung;

import android.content.Context;
import com.tradehero.common.billing.samsung.BaseSamsungPurchaseFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;

public class THBaseSamsungPurchaseFetcherHolder
    extends BaseSamsungPurchaseFetcherHolder<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        THSamsungPurchaseFetcher,
        SamsungException>
    implements THSamsungPurchaseFetcherHolder
{
    @NonNull protected final Context context;
    protected final int mode;
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaseFetcherHolder(
            @NonNull Context context,
            @ForSamsungBillingMode int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory,
            @NonNull @ProcessingPurchase StringSetPreference processingPurchaseStringSet)
    {
        super();
        this.context = context;
        this.mode = mode;
        this.samsungExceptionFactory = samsungExceptionFactory;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
    }
    //</editor-fold>

    @NonNull @Override protected THSamsungPurchaseFetcher createPurchaseFetcher(int requestCode)
    {
        return new THBaseSamsungPurchaseFetcher(requestCode, context, mode, samsungExceptionFactory, processingPurchaseStringSet);
    }
}
