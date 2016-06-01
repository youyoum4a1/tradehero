package com.ayondo.academy.billing.amazon.consume;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonPurchaseCacheRx;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.consume.BaseAmazonPurchaseConsumerRx;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.persistence.billing.THAmazonPurchaseCacheRx;
import javax.inject.Inject;

public class THBaseAmazonPurchaseConsumerRx
        extends BaseAmazonPurchaseConsumerRx<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase>
        implements THAmazonPurchaseConsumerRx
{
    @NonNull protected final THAmazonPurchaseCacheRx thAmazonPurchaseCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseConsumerRx(
            int request,
            @NonNull THAmazonPurchase purchase,
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonPurchaseCacheRx thAmazonPurchaseCache)
    {
        super(request, purchase, purchasingService);
        this.thAmazonPurchaseCache = thAmazonPurchaseCache;
    }
    //</editor-fold>

    @Override @NonNull protected AmazonPurchaseCacheRx<AmazonSKU, THAmazonOrderId, THAmazonPurchase> getPurchaseCache()
    {
        return thAmazonPurchaseCache;
    }
}
