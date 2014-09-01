package com.tradehero.th.billing.amazon;

import android.content.Context;
import com.tradehero.common.billing.amazon.AmazonPurchaseCache;
import com.tradehero.common.billing.amazon.AmazonPurchasingService;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseConsumer;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import com.tradehero.th.persistence.billing.THAmazonPurchaseCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class THBaseAmazonPurchaseConsumer
        extends BaseAmazonPurchaseConsumer<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase>
    implements THAmazonPurchaseConsumer
{
    @NotNull protected final THAmazonExceptionFactory thAmazonExceptionFactory;
    @NotNull protected final THAmazonPurchaseCache thiabPurchaseCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseConsumer(
            @NotNull Context appContext,
            @NotNull AmazonPurchasingService purchasingService,
            @NotNull THAmazonExceptionFactory amazonExceptionFactory,
            @NotNull THAmazonPurchaseCache thAmazonPurchaseCache)
    {
        super(appContext, purchasingService);
        this.thAmazonExceptionFactory = amazonExceptionFactory;
        this.thiabPurchaseCache = thAmazonPurchaseCache;
    }
    //</editor-fold>

    @Override @NotNull protected AmazonPurchaseCache<AmazonSKU, THAmazonOrderId, THAmazonPurchase> getPurchaseCache()
    {
        return thiabPurchaseCache;
    }
}
