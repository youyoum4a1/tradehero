package com.tradehero.th.billing.amazon;

import android.content.Context;
import com.tradehero.common.billing.amazon.AmazonPurchaseCache;
import com.tradehero.common.billing.amazon.AmazonPurchasingService;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseConsumer;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import com.tradehero.th.persistence.billing.THAmazonPurchaseCache;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class THBaseAmazonPurchaseConsumer
        extends BaseAmazonPurchaseConsumer<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase>
    implements THAmazonPurchaseConsumer
{
    @NonNull protected final THAmazonExceptionFactory thAmazonExceptionFactory;
    @NonNull protected final THAmazonPurchaseCache thiabPurchaseCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseConsumer(
            @NonNull Context appContext,
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonExceptionFactory amazonExceptionFactory,
            @NonNull THAmazonPurchaseCache thAmazonPurchaseCache)
    {
        super(appContext, purchasingService);
        this.thAmazonExceptionFactory = amazonExceptionFactory;
        this.thiabPurchaseCache = thAmazonPurchaseCache;
    }
    //</editor-fold>

    @Override @NonNull protected AmazonPurchaseCache<AmazonSKU, THAmazonOrderId, THAmazonPurchase> getPurchaseCache()
    {
        return thiabPurchaseCache;
    }
}
