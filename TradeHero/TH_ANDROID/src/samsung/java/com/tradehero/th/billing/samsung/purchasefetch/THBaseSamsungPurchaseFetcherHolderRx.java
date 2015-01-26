package com.tradehero.th.billing.samsung.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchasefetch.BaseSamsungPurchaseFetcherHolderRx;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.billing.samsung.ForSamsungBillingMode;
import com.tradehero.th.billing.samsung.ProcessingPurchase;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import javax.inject.Inject;

public class THBaseSamsungPurchaseFetcherHolderRx
        extends BaseSamsungPurchaseFetcherHolderRx<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase>
        implements THSamsungPurchaseFetcherHolderRx
{
    @NonNull protected final Context context;
    protected final int mode;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaseFetcherHolderRx(
            @NonNull Context context,
            @ForSamsungBillingMode int mode,
            @NonNull @ProcessingPurchase StringSetPreference processingPurchaseStringSet)
    {
        super();
        this.context = context;
        this.mode = mode;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
    }
    //</editor-fold>

    @NonNull @Override protected THSamsungPurchaseFetcherRx createFetcher(int requestCode)
    {
        return new THBaseSamsungPurchaseFetcherRx(requestCode, context, mode, processingPurchaseStringSet);
    }
}
