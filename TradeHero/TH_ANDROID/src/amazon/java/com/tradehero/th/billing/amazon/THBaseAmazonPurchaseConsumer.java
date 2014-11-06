package com.tradehero.th.billing.amazon;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonPurchaseCacheRx;
import com.tradehero.common.billing.amazon.AmazonPurchasingService;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseConsumer;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import com.tradehero.th.persistence.billing.THAmazonPurchaseCacheRx;
import javax.inject.Inject;

public class THBaseAmazonPurchaseConsumer
        extends BaseAmazonPurchaseConsumer<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase>
    implements THAmazonPurchaseConsumer
{
    @NonNull protected final THAmazonExceptionFactory thAmazonExceptionFactory;
    @NonNull protected final THAmazonPurchaseCacheRx thAmazonPurchaseCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseConsumer(
            @NonNull Context appContext,
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonExceptionFactory amazonExceptionFactory,
            @NonNull THAmazonPurchaseCacheRx thAmazonPurchaseCache)
    {
        super(appContext, purchasingService);
        this.thAmazonExceptionFactory = amazonExceptionFactory;
        this.thAmazonPurchaseCache = thAmazonPurchaseCache;
    }
    //</editor-fold>

    @Override @NonNull protected AmazonPurchaseCacheRx<AmazonSKU, THAmazonOrderId, THAmazonPurchase> getPurchaseCache()
    {
        return thAmazonPurchaseCache;
    }
}
