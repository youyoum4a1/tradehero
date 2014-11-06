package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.persistence.billing.googleplay.IABPurchaseCacheRx;
import com.tradehero.th.persistence.billing.googleplay.THIABPurchaseCacheRx;
import dagger.Lazy;
import javax.inject.Inject;

public class THBaseIABPurchaseConsumer
        extends BaseIABPurchaseConsumer<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
    implements THIABPurchaseConsumer
{
    @NonNull protected final THIABPurchaseCacheRx thiabPurchaseCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseConsumer(
            @NonNull Context context,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory,
            @NonNull THIABPurchaseCacheRx thiabPurchaseCache)
    {
        super(context, iabExceptionFactory);
        this.thiabPurchaseCache = thiabPurchaseCache;
    }
    //</editor-fold>

    @Override @NonNull protected IABPurchaseCacheRx<IABSKU, THIABOrderId, THIABPurchase> getPurchaseCache()
    {
        return thiabPurchaseCache;
    }
}
