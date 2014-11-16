package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABPurchaserHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseIABPurchaserHolder
        extends BaseIABPurchaserHolder<
        IABSKU,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        THIABPurchaser,
        IABException>
        implements THIABPurchaserHolder
{
    @NonNull protected final Provider<Activity> activityProvider;
    @NonNull protected final Lazy<IABExceptionFactory> iabExceptionFactory;
    @NonNull protected final Lazy<THIABProductDetailCacheRx> skuDetailCache;

    //<editor-fold desc="Constructors">
    @Inject THBaseIABPurchaserHolder(
            @NonNull Provider<Activity> activityProvider,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory,
            @NonNull Lazy<THIABProductDetailCacheRx> skuDetailCache)
    {
        super();
        this.activityProvider = activityProvider;
        this.iabExceptionFactory = iabExceptionFactory;
        this.skuDetailCache = skuDetailCache;
    }
    //</editor-fold>

    @Override @NonNull protected THIABPurchaser createPurchaser(int requestCode)
    {
        return new THBaseIABPurchaser(requestCode, activityProvider.get(), iabExceptionFactory, skuDetailCache);
    }
}
